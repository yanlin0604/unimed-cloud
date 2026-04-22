package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.entity.ChEducationRule;
import org.dromara.chronic.domain.entity.ChHealthEducationDelivery;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.mapper.ChEducationRuleMapper;
import org.dromara.chronic.mapper.ChHealthEducationDeliveryMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.resource.api.RemoteMessageService;
import org.dromara.resource.api.RemoteSmsService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 宣教推送管理器：规则匹配→通道选择→投递→阅读回执
 * <p>
 * R11: 基于规则引擎匹配患者特征，多通道（站内消息/短信/微信）推送宣教内容。
 * 规则表达式使用 SpEL 格式，对患者上下文求值判定是否命中。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EducationPushManager {

    private final ChEducationRuleMapper educationRuleMapper;
    private final ChHealthEducationDeliveryMapper deliveryMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    @DubboReference(mock = "org.dromara.resource.api.RemoteMessageServiceStub")
    private RemoteMessageService remoteMessageService;
    @DubboReference(mock = "true")
    private RemoteSmsService remoteSmsService;
    @DubboReference
    private RemoteUserService remoteUserService;

    /**
     * R11: 基于规则引擎匹配推送 —— 遍历激活规则，对患者上下文求值，命中则多通道投递
     */
    @Transactional(rollbackFor = Exception.class)
    public int pushByRules(Long patientId) {
        ChPatientProfile patient = patientProfileMapper.selectById(patientId);
        if (patient == null) {
            return 0;
        }
        Map<String, Object> context = Map.of(
            "patientId", patient.getPatientId(),
            "manageStatus", patient.getManageStatus() != null ? patient.getManageStatus() : "",
            "orgId", patient.getOrgId() != null ? patient.getOrgId() : 0L
        );
        List<ChEducationRule> activeRules = getActiveRules();
        int pushed = 0;
        for (ChEducationRule rule : activeRules) {
            if (evaluateCondition(rule.getConditionExpression(), context)) {
                pushToPatient(rule.getTemplateId(), patientId, "RULE_ENGINE", rule.getPushChannel());
                pushMultiChannel(patient, rule);
                pushed++;
            }
        }
        return pushed;
    }

    /**
     * 按规则匹配推送宣教内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Long pushToPatient(Long contentId, Long patientId, String triggerType, String pushChannel) {
        ChHealthEducationDelivery delivery = new ChHealthEducationDelivery();
        delivery.setContentId(contentId);
        delivery.setPatientId(patientId);
        delivery.setTriggerType(triggerType);
        delivery.setPushChannel(pushChannel);
        delivery.setDeliveryStatus("DELIVERED");
        delivery.setReadStatus(false);
        deliveryMapper.insert(delivery);
        return delivery.getDeliveryId();
    }

    /**
     * 批量推送宣教内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Void broadcastToPatients(Long contentId, List<Long> patientIds, String triggerType, String pushChannel) {
        for (Long patientId : patientIds) {
            pushToPatient(contentId, patientId, triggerType, pushChannel);
        }
        return null;
    }

    /**
     * 获取所有激活的宣教规则
     */
    public List<ChEducationRule> getActiveRules() {
        return educationRuleMapper.selectList(
            Wrappers.<ChEducationRule>lambdaQuery().eq(ChEducationRule::getIsActive, true)
        );
    }

    /**
     * R11: 多通道推送 —— 站内消息 + 短信 + 微信（按规则配置）
     * <p>
     * 注意：站内消息推送目标为患者的主治医生（doctorUserId），非患者本人，
     * 因为患者通常无系统账号；短信则推送给医生手机。
     */
    private void pushMultiChannel(ChPatientProfile patient, ChEducationRule rule) {
        // R11: 推送给患者的责任医生，而非患者本人（患者无系统账号）
        Long patientId = patient.getPatientId();
        Long doctorUserId = patient.getDoctorUserId();
        if (doctorUserId == null) {
            log.warn("宣教推送跳过: 患者无责任医生 patientId={}", patientId);
            return;
        }
        String channel = rule.getPushChannel();
        String message = "您的患者(ID:" + patientId + ")有新的健康宣教内容，请关注。";
        try {
            if ("WECHAT".equals(channel) || "ALL".equals(channel)) {
                // 站内消息推送给医生（SSE/WebSocket）
                remoteMessageService.publishMessage(List.of(doctorUserId), message);
            }
            if ("SMS".equals(channel) || "ALL".equals(channel)) {
                // 短信推送给医生
                String phone = lookupPhone(doctorUserId);
                if (phone != null) {
                    remoteSmsService.sendMessageAsync(phone, message);
                }
            }
        } catch (Exception e) {
            log.warn("宣教推送多通道投递失败 patientId={} ruleId={} msg={}", patientId, rule.getRuleId(), e.getMessage());
        }
    }

    /**
     * R11: SpEL 条件求值（基于患者上下文 Map）
     * <p>
     * 使用 rootObject 属性访问模式，支持如: manageStatus == 'MANAGED', orgId == 100 等表达式。
     * 空条件或解析失败时返回 false，不中断推送流程。
     */
    private boolean evaluateCondition(String expression, Map<String, Object> context) {
        if (expression == null || expression.isBlank()) {
            return false;
        }
        try {
            org.springframework.expression.spel.standard.SpelExpressionParser parser =
                new org.springframework.expression.spel.standard.SpelExpressionParser();
            org.springframework.expression.Expression exp = parser.parseExpression(expression);
            org.springframework.expression.spel.support.StandardEvaluationContext evalCtx =
                new org.springframework.expression.spel.support.StandardEvaluationContext(context);
            Boolean result = exp.getValue(evalCtx, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("宣教规则表达式求值失败 expression={} msg={}", expression, e.getMessage());
            return false;
        }
    }

    /**
     * 查询用户手机号
     */
    private String lookupPhone(Long userId) {
        try {
            List<RemoteUserVo> users = remoteUserService.selectListByIds(List.of(userId));
            if (users != null && !users.isEmpty()) {
                return users.get(0).getPhonenumber();
            }
        } catch (Exception e) {
            log.warn("查询用户手机号失败 userId={} msg={}", userId, e.getMessage());
        }
        return null;
    }
}
