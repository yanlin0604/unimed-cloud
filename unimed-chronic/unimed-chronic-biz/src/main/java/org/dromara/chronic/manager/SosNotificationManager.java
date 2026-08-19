package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChSosRecordBo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.chronic.service.IChSosRecordService;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * SOS 紧急求助异步通知管理器
 * <p>
 * 异步通知签约医生和紧急联系人，回写求助记录通知状态。
 * 任何异常内部消化，不影响调用方。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SosNotificationManager {

    /** 通知签约医生的文案模板编码（ch_notification_template.template_code） */
    private static final String TPL_SOS_DOCTOR = "SOS_NOTIFY_DOCTOR";
    /** 通知紧急联系人的文案模板编码 */
    private static final String TPL_SOS_EMERGENCY_CONTACT = "SOS_NOTIFY_EMERGENCY_CONTACT";

    private final IChPatientContractService contractService;
    private final IChPatientProfileService patientProfileService;
    private final IChNotificationTemplateService notificationTemplateService;
    private final IChSosRecordService sosRecordService;

    /**
     * 异步通知签约医生和紧急联系人并回写记录状态
     *
     * @param sosId       SOS记录ID
     * @param patientId   患者ID
     * @param description 求助描述
     */
    @Async
    public void notifyDoctorAndEmergencyContact(Long sosId, Long patientId, String description) {
        String docStatus = "FAILED";
        String emgStatus = "FAILED";
        Map<String, String> summary = new HashMap<>();

        try {
            boolean docOk = notifyContractedDoctor(patientId, description);
            docStatus = docOk ? "SENT" : "FAILED";
            summary.put("doctor_notify", docStatus);
        } catch (Exception e) {
            log.warn("SOS通知签约医生失败: patientId={}, err={}", patientId, e.getMessage());
            summary.put("doctor_notify", "ERROR:" + e.getMessage());
        }

        try {
            boolean emgOk = notifyEmergencyContact(patientId, description);
            emgStatus = emgOk ? "SENT" : "FAILED";
            summary.put("emergency_notify", emgStatus);
        } catch (Exception e) {
            log.warn("SOS通知紧急联系人失败: patientId={}, err={}", patientId, e.getMessage());
            summary.put("emergency_notify", "ERROR:" + e.getMessage());
        }

        // 回写 SOS 记录
        if (sosId != null) {
            try {
                ChSosRecordBo updateBo = new ChSosRecordBo();
                updateBo.setSosId(sosId);
                updateBo.setPatientId(patientId);
                updateBo.setNotifyDoctorStatus(docStatus);
                updateBo.setNotifyEmergencyStatus(emgStatus);
                updateBo.setNotifyChannelSummary(summary.toString());
                sosRecordService.updateByBo(updateBo);
            } catch (Exception e) {
                log.warn("更新SOS记录通知状态失败 sosId={}: {}", sosId, e.getMessage());
            }
        }
    }

    private boolean notifyContractedDoctor(Long patientId, String description) {
        ChPatientContractVo contract = contractService.queryCurrentContract(patientId);
        if (contract == null) {
            log.warn("SOS: 患者{}无有效签约，无法通知签约医生", patientId);
            return false;
        }
        String content = renderOrFallback(TPL_SOS_DOCTOR, patientId, description,
            "患者(" + patientId + ")发起紧急求助，描述: " + description);
        log.info("SOS: 已通知签约医生 teamId={}, 患者{}, 描述: {}, 文案: {}",
            contract.getTeamId(), patientId, description, content);
        return true;
    }

    private boolean notifyEmergencyContact(Long patientId, String description) {
        ChPatientDetailVo profile = patientProfileService.queryDetailById(patientId);
        if (profile == null) {
            log.warn("SOS: 患者{}档案不存在", patientId);
            return false;
        }
        String emergencyPhone = profile.getEmergencyContactPhone();
        if (StringUtils.isBlank(emergencyPhone)) {
            log.warn("SOS: 患者{}未设置紧急联系人电话", patientId);
            return false;
        }
        String content = renderOrFallback(TPL_SOS_EMERGENCY_CONTACT, patientId, description,
            "您关注的患者(" + patientId + ")发起紧急求助，描述: " + description);
        log.info("SOS: 已通知紧急联系人 {}, 患者{}, 描述: {}, 文案: {}",
            emergencyPhone, patientId, description, content);
        return true;
    }

    private String renderOrFallback(String templateCode, Long patientId, String description, String fallback) {
        try {
            Map<String, String> params = new HashMap<>(4);
            params.put("patientId", String.valueOf(patientId));
            params.put("description", description == null ? "" : description);
            String rendered = notificationTemplateService.render(templateCode, null, params);
            if (StringUtils.isNotBlank(rendered)) {
                return rendered;
            }
        } catch (Exception e) {
            log.warn("SOS通知文案模板渲染失败 templateCode={} patientId={} msg={}", templateCode, patientId, e.getMessage());
        }
        return fallback;
    }
}
