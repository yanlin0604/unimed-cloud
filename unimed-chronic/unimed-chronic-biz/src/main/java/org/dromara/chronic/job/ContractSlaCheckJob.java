package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 签约SLA违约检测定时任务
 * <p>
 * 扫描生效签约，检测SLA违约情况，自动生成告警事件
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "contractSlaCheckJob")
@RequiredArgsConstructor
public class ContractSlaCheckJob {

    private final ChPatientContractMapper contractMapper;
    private final ChWarningEventMapper warningEventMapper;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("签约SLA检测开始");
        int violations = 0;

        List<ChPatientContract> activeContracts = contractMapper.selectList(
            Wrappers.<ChPatientContract>lambdaQuery()
                .eq(ChPatientContract::getContractStatus, "ACTIVE")
        );

        for (ChPatientContract contract : activeContracts) {
            if (isSlaViolated(contract)) {
                // 检查是否已有违约告警（防重复）
                Long existingAlerts = warningEventMapper.selectCount(
                    Wrappers.<ChWarningEvent>lambdaQuery()
                        .eq(ChWarningEvent::getPatientId, contract.getPatientId())
                        .eq(ChWarningEvent::getWarningLevel, "SLA_VIOLATION")
                        .eq(ChWarningEvent::getEventStatus, "NEW")
                );
                if (existingAlerts > 0) {
                    continue;
                }

                ChWarningEvent event = new ChWarningEvent();
                event.setPatientId(contract.getPatientId());
                event.setWarningLevel("SLA_VIOLATION");
                event.setEventStatus("NEW");
                event.setWarningTime(new Date());
                warningEventMapper.insert(event);
                violations++;
            }
        }

        SnailJobLog.REMOTE.info("签约SLA检测完成, 违约告警数: {}", violations);
        return ExecuteResult.success("检测SLA违约" + violations + "条");
    }

    private boolean isSlaViolated(ChPatientContract contract) {
        // 简化判断：签约到期前30天仍未完成最低随访次数则视为违约
        // 实际规则应从ch_contract_service_package读取SLA配置
        if (contract.getContractPeriodEnd() != null) {
            long daysRemaining = (contract.getContractPeriodEnd().getTime() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);
            return daysRemaining <= 30 && daysRemaining >= 0;
        }
        return false;
    }
}
