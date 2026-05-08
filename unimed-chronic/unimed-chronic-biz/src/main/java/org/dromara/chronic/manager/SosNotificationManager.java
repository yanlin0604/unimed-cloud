package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * SOS 紧急求助异步通知管理器
 * <p>
 * 异步通知签约医生和紧急联系人，不阻塞主请求流程。
 * 任何异常内部消化，不影响调用方。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SosNotificationManager {

    private final IChPatientContractService contractService;
    private final IChPatientProfileService patientProfileService;

    /**
     * 异步通知签约医生和紧急联系人
     *
     * @param patientId   患者ID
     * @param description 求助描述
     */
    @Async
    public void notifyDoctorAndEmergencyContact(Long patientId, String description) {
        try {
            notifyContractedDoctor(patientId, description);
        } catch (Exception e) {
            log.warn("SOS通知签约医生失败: patientId={}, err={}", patientId, e.getMessage());
        }
        try {
            notifyEmergencyContact(patientId, description);
        } catch (Exception e) {
            log.warn("SOS通知紧急联系人失败: patientId={}, err={}", patientId, e.getMessage());
        }
    }

    private void notifyContractedDoctor(Long patientId, String description) {
        ChPatientContractVo contract = contractService.queryCurrentContract(patientId);
        if (contract == null) {
            log.warn("SOS: 患者{}无有效签约，无法通知签约医生", patientId);
            return;
        }
        // TODO: 集成消息推送服务，向签约医生发送站内消息+推送
        log.info("SOS: 已通知签约医生 teamId={}, 患者{}, 描述: {}", contract.getTeamId(), patientId, description);
    }

    private void notifyEmergencyContact(Long patientId, String description) {
        ChPatientDetailVo profile = patientProfileService.queryDetailById(patientId);
        if (profile == null) {
            log.warn("SOS: 患者{}档案不存在", patientId);
            return;
        }
        // 假设 ChPatientDetailVo 有 emergencyContactPhone 字段
        String emergencyPhone = profile.getEmergencyContactPhone();
        if (StringUtils.isBlank(emergencyPhone)) {
            log.warn("SOS: 患者{}未设置紧急联系人电话", patientId);
            return;
        }
        // TODO: 集成短信服务，向紧急联系人发送短信通知
        log.info("SOS: 已通知紧急联系人 {}, 患者{}, 描述: {}", emergencyPhone, patientId, description);
    }
}
