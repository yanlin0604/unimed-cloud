package org.dromara.chronic.manager;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * 慢病多通道统一推送管理器 (微信服务号/小程序订阅消息 + 短信兜底)
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChronicPushNotificationManager {

    /**
     * 异步多通道调度分发
     */
    @Async
    public void pushMultiChannel(String phone, String openid, String templateId, String title, String content, Map<String, String> params) {
        boolean wechatSuccess = false;
        // 1. 优先尝试微信通道 (微信服务号模板消息或小程序订阅消息)
        if (StrUtil.isNotBlank(openid)) {
            try {
                wechatSuccess = sendWechatMessage(openid, templateId, title, content, params);
                if (wechatSuccess) {
                    log.info("微信通道消息推送成功: openid={}, title={}", openid, title);
                    return;
                }
            } catch (Exception e) {
                log.warn("微信通道推送异常, 准备降级至短信通道: openid={}, error={}", openid, e.getMessage());
            }
        }

        // 2. 降级短信通道兜底
        if (StrUtil.isNotBlank(phone)) {
            try {
                sendSmsFallback(phone, title, content, params);
                log.info("短信通道兜底推送成功: phone={}, title={}", phone, title);
            } catch (Exception e) {
                log.error("短信通道推送失败: phone={}, error={}", phone, e.getMessage(), e);
            }
        }
    }

    /**
     * 1. 危急值与指标偏高预警推送
     */
    public void notifyEarlyWarning(Long patientId, String phone, String openid, String metricType, String metricValue, String alertLevel) {
        String title = "【慢病预警提醒】您的体征指标偏高";
        String content = StrUtil.format("尊敬的患者，您最新测量的{}为{}，处于{}状态，请注意休息或及时咨询责任医生。", metricType, metricValue, alertLevel);
        pushMultiChannel(phone, openid, "WECHAT_TPL_WARNING", title, content, Map.of(
            "metricType", metricType,
            "metricValue", metricValue,
            "alertLevel", alertLevel
        ));
    }

    /**
     * 2. 随访任务到期催办提醒
     */
    public void notifyFollowupDue(Long patientId, String phone, String openid, String planName, LocalDate dueDate) {
        String title = "【随访到期提醒】您的慢病定期随访已到期";
        String content = StrUtil.format("您有【{}】随访任务将于{}到期，请及时在小程序中完成自我随访评估。", planName, dueDate);
        pushMultiChannel(phone, openid, "WECHAT_TPL_FOLLOWUP", title, content, Map.of(
            "planName", planName,
            "dueDate", String.valueOf(dueDate)
        ));
    }

    /**
     * 3. 连续多日未测量体征打卡提醒
     */
    public void notifyMissingMeasurement(Long patientId, String phone, String openid, int missingDays) {
        String title = "【健康打卡提醒】您已连续未测体征";
        String content = StrUtil.format("您已连续{}天未记录血压/血糖数据，规律监测有助于稳定控制慢病，请今日抽空测量。", missingDays);
        pushMultiChannel(phone, openid, "WECHAT_TPL_MEASURE", title, content, Map.of(
            "missingDays", String.valueOf(missingDays)
        ));
    }

    private boolean sendWechatMessage(String openid, String templateId, String title, String content, Map<String, String> params) {
        // 模拟/封装微信开放平台 API 调用
        log.info("[微信服务号/小程序推送] 投递目标 openid={}, templateId={}, title={}", openid, templateId, title);
        return true;
    }

    private void sendSmsFallback(String phone, String title, String content, Map<String, String> params) {
        // 模拟/封装短信网关 API 调用
        log.info("[短信网关降级兜底] 发送手机号 phone={}, content={}", phone, content);
    }
}
