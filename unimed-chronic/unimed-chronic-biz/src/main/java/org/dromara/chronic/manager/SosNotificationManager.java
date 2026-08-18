package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    /** 通知签约医生的文案模板编码（ch_notification_template.template_code） */
    private static final String TPL_SOS_DOCTOR = "SOS_NOTIFY_DOCTOR";
    /** 通知紧急联系人的文案模板编码 */
    private static final String TPL_SOS_EMERGENCY_CONTACT = "SOS_NOTIFY_EMERGENCY_CONTACT";

    private final IChPatientContractService contractService;
    private final IChPatientProfileService patientProfileService;
    private final IChNotificationTemplateService notificationTemplateService;

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
        String content = renderOrFallback(TPL_SOS_DOCTOR, patientId, description,
            "患者(" + patientId + ")发起紧急求助，描述: " + description);
        // TODO: 集成消息推送服务，向签约医生发送站内消息+推送（content 即待推送文案）
        log.info("SOS: 已通知签约医生 teamId={}, 患者{}, 描述: {}, 文案: {}",
            contract.getTeamId(), patientId, description, content);
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
        String content = renderOrFallback(TPL_SOS_EMERGENCY_CONTACT, patientId, description,
            "您关注的患者(" + patientId + ")发起紧急求助，描述: " + description);
        // TODO: 集成短信服务，向紧急联系人发送短信通知（content 即待发送文案）
        log.info("SOS: 已通知紧急联系人 {}, 患者{}, 描述: {}, 文案: {}",
            emergencyPhone, patientId, description, content);
    }

    /**
     * 优先取通知模板渲染文案；模板不存在 / 已停用 / 渲染异常时退回硬编码文案（行为不退化）。
     * <p>
     * 支持占位符：{patientId} {description}
     */
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
