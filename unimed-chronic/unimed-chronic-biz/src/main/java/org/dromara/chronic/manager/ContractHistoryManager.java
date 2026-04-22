package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChPatientTimelineService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.resource.api.RemoteMessageService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 签约历史与续约提醒管理器
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractHistoryManager {

    private final IChPatientContractService contractService;
    private final IChPatientTimelineService timelineService;
    private final ChAuditLogMapper auditLogMapper;
    @DubboReference(mock = "org.dromara.resource.api.RemoteMessageServiceStub")
    private RemoteMessageService remoteMessageService;
    private final ChPatientProfileMapper patientProfileMapper;

    public TableDataInfo<ChPatientTimelineVo> queryContractTimeline(Long patientId, PageQuery pageQuery) {
        // 只返回签约相关事件（SIGN / RENEWAL_REMINDER / CONTRACT_EXPIRED）
        return timelineService.queryPageListByEventTypes(patientId,
            List.of("SIGN", "RENEWAL_REMINDER", "CONTRACT_EXPIRED"), pageQuery);
    }

    public List<ChPatientTimelineVo> queryContractHistory(Long patientId, String eventType, Integer limit) {
        return timelineService.queryList(patientId, eventType, limit);
    }

    public ChPatientContractVo queryCurrentContract(Long patientId) {
        return contractService.queryCurrentContract(patientId);
    }

    public List<ChContractFulfillmentVo> queryFulfillmentList(Long contractId) {
        return contractService.queryFulfillmentList(contractId);
    }

    public Void sendRenewalReminder(Long contractId) {
        try {
            ChPatientContractVo contract = contractService.queryById(contractId);
            if (contract == null) {
                log.warn("续约提醒失败，签约不存在: contractId={}", contractId);
                return null;
            }
            // 1. 写入时间线事件
            timelineService.recordEvent(
                contract.getPatientId(),
                "RENEWAL_REMINDER",
                "续约提醒",
                "合同即将到期，请关注续约事宜",
                java.time.LocalDateTime.now()
            );
            // 2. 推送消息通知给责任医生
            try {
                ChPatientProfile profile = patientProfileMapper.selectById(contract.getPatientId());
                if (profile != null && profile.getDoctorUserId() != null) {
                    remoteMessageService.publishMessage(
                        List.of(profile.getDoctorUserId()),
                        "签约即将到期，请提醒患者续约。合同ID: " + contractId
                    );
                }
            } catch (Exception msgEx) {
                log.warn("续约提醒消息推送失败, contractId={}", contractId, msgEx);
            }
            // 3. 写入审计日志
            logAudit("CONTRACT_RENEWAL_REMIND", "CONTRACT",
                "发送续约提醒: contractId=" + contractId + ", patientId=" + contract.getPatientId());
        } catch (Exception e) {
            log.warn("续约提醒发送失败, contractId={}", contractId, e);
        }
        return null;
    }

    private void logAudit(String operationType, String operationTarget, String detail) {
        try {
            ChAuditLog auditLog = new ChAuditLog();
            auditLog.setOperationType(operationType);
            auditLog.setOperationTarget(operationTarget);
            auditLog.setOperationDetail(detail);
            auditLog.setOperatorId(LoginHelper.getUserId());
            auditLog.setOperatorName(LoginHelper.getUsername());
            auditLog.setOperatorIp(ServletUtils.getClientIP());
            auditLog.setOperationTime(new Date());
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.warn("审计日志写入失败", e);
        }
    }
}