package org.dromara.chronic.job;

import cn.hutool.core.lang.Dict;
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChContractFulfillment;
import org.dromara.chronic.domain.entity.ChContractServicePackage;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChContractFulfillmentMapper;
import org.dromara.chronic.mapper.ChContractServicePackageMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 签约SLA违约检测定时任务
 * <p>
 * R6: 扫描生效签约，从 ch_contract_service_package.service_items 读取年度随访次数阈值，
 * 与 ch_contract_fulfillment 已完成次数对比，结合到期天数判定 SLA 违约；
 * 另行检测即将到期签约并发送续约提醒（幂等置位 expiry_remind_status）。
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "contractSlaCheckJob")
@RequiredArgsConstructor
public class ContractSlaCheckJob {

    /** 到期前 N 天开始预警 */
    private static final long SLA_WARN_DAYS = 30;

    /**
     * SLA 违约事件的标记值，写入 ch_warning_event.warning_value。
     * <p>
     * 不能写进 warning_level —— 那是严重程度字段，值域受字典 chronic_warning_level
     * （LOW/MEDIUM/HIGH/CRITICAL）约束，写非法值会导致前端等级列翻译为空白。
     */
    private static final String SLA_VIOLATION_FLAG = "SLA_VIOLATION";

    private final ChPatientContractMapper contractMapper;
    private final ChWarningEventMapper warningEventMapper;
    private final IChWarningEventService warningEventService;
    private final ChContractServicePackageMapper packageMapper;
    private final ChContractFulfillmentMapper fulfillmentMapper;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("签约SLA检测开始");
        int violations = 0;
        int reminders = 0;

        List<ChPatientContract> activeContracts = contractMapper.selectList(
            Wrappers.<ChPatientContract>lambdaQuery()
                .eq(ChPatientContract::getContractStatus, "ACTIVE")
        );

        for (ChPatientContract contract : activeContracts) {
            // —— SLA 违约检测 ——
            if (isSlaViolated(contract)) {
                // R6 AC3: 同一签约的未关闭 SLA 事件不重复生成。
                Long slaAlertsForContract = warningEventMapper.selectCount(
                    Wrappers.<ChWarningEvent>lambdaQuery()
                        .eq(ChWarningEvent::getPatientId, contract.getPatientId())
                        .eq(ChWarningEvent::getEventSource, "SLA")
                        .eq(ChWarningEvent::getSourceId, contract.getContractId())
                        .eq(ChWarningEvent::getWarningValue, SLA_VIOLATION_FLAG)
                        .notIn(ChWarningEvent::getEventStatus, List.of("RESOLVED", "ARCHIVED"))
                );
                if (slaAlertsForContract == 0) {
                    ChWarningEventBo event = new ChWarningEventBo();
                    event.setPatientId(contract.getPatientId());
                    event.setRuleId(null);
                    event.setEventSource("SLA");
                    event.setSourceId(contract.getContractId());
                    // warningLevel 是**严重程度**字段，值域由字典 chronic_warning_level 约束
                    // （LOW/MEDIUM/HIGH/CRITICAL）。原实现往这里写事件类型 "SLA_VIOLATION"，
                    // 该值不在字典内，会导致前端预警列表的「等级」列因 @Translation 翻译不出而空白。
                    // 现改为：等级取 MEDIUM（签约履约不达标属管理问题，非临床危急），
                    // 事件类型改存 warningValue（该字段本就用于承载触发值/原因）。
                    event.setWarningLevel("MEDIUM");
                    event.setWarningValue(SLA_VIOLATION_FLAG);
                    event.setEventStatus("NEW");
                    warningEventService.createEvent(event);
                    violations++;
                }
            }

            // —— 续约到期提醒（幂等） ——
            if (shouldSendRenewalReminder(contract)) {
                contract.setExpiryRemindStatus(true);
                contractMapper.updateById(contract);
                // TODO: 对接 RemoteMessageService 发送续约提醒通知
                reminders++;
            }
        }

        SnailJobLog.REMOTE.info("签约SLA检测完成, 违约告警数: {}, 续约提醒数: {}", violations, reminders);
        return ExecuteResult.success("SLA违约" + violations + "条, 续约提醒" + reminders + "条");
    }

    /**
     * R6: 数据驱动的 SLA 违约判定
     * <p>
     * 从签约关联的服务包 service_items JSON 中读取年度随访次数阈值，
     * 与已完成履约次数比较，叠加到期天数条件。
     */
    private boolean isSlaViolated(ChPatientContract contract) {
        if (contract.getContractPeriodEnd() == null || contract.getPackageId() == null) {
            return false;
        }
        long daysRemaining = (contract.getContractPeriodEnd().getTime() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);
        if (daysRemaining > SLA_WARN_DAYS || daysRemaining < 0) {
            return false;
        }
        // 从服务包读取 SLA 阈值
        Integer requiredFollowupCount = getRequiredFollowupCount(contract.getPackageId());
        if (requiredFollowupCount == null || requiredFollowupCount <= 0) {
            return false;
        }
        // 统计已完成随访次数
        long doneCount = fulfillmentMapper.selectCount(
            Wrappers.<ChContractFulfillment>lambdaQuery()
                .eq(ChContractFulfillment::getContractId, contract.getContractId())
                .eq(ChContractFulfillment::getFulfillmentStatus, "DONE")
        );
        return doneCount < requiredFollowupCount;
    }

    /**
     * 从 ch_contract_service_package.service_items JSON 解析年度随访次数阈值
     * <p>
     * JSON 示例: {"annualFollowupCount": 4, "responseHours": 24}
     */
    private Integer getRequiredFollowupCount(Long packageId) {
        ChContractServicePackage pkg = packageMapper.selectById(packageId);
        if (pkg == null || pkg.getServiceItems() == null) {
            return null;
        }
        try {
            Dict items = JsonUtils.parseMap(pkg.getServiceItems());
            if (items == null) {
                return null;
            }
            Object count = items.get("annualFollowupCount");
            if (count instanceof Number number) {
                return number.intValue();
            }
            return null;
        } catch (Exception ex) {
            SnailJobLog.LOCAL.warn("解析 service_items JSON 失败 packageId={} msg={}", packageId, ex.getMessage());
            return null;
        }
    }

    /**
     * R6: 到期前 30 天 + expiry_remind_status=false → 应发续约提醒
     */
    private boolean shouldSendRenewalReminder(ChPatientContract contract) {
        if (contract.getContractPeriodEnd() == null) {
            return false;
        }
        if (Boolean.TRUE.equals(contract.getExpiryRemindStatus())) {
            return false; // 已提醒，幂等跳过
        }
        long daysRemaining = (contract.getContractPeriodEnd().getTime() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);
        return daysRemaining <= SLA_WARN_DAYS && daysRemaining >= 0;
    }
}
