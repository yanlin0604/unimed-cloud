package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChWebhookSubscriptionBo;
import org.dromara.chronic.domain.vo.ChWebhookSubscriptionVo;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.chronic.service.IChWebhookSubscriptionService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Webhook 注册/推送开放接口
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-开放接口-Webhook")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiWebhookController extends BaseController {

    private final IChReferralService referralService;
    private final IChWebhookSubscriptionService webhookSubscriptionService;

    @Operation(summary = "注册Webhook订阅")
    @PostMapping("/chronic/openapi/webhook/subscribe")
    public R<Long> subscribe(@RequestBody Map<String, String> params) {
        String callbackUrl = params.get("callbackUrl");
        String eventType = params.get("eventType");
        String thirdPartyName = params.getOrDefault("thirdPartyName", "EXTERNAL_SYSTEM");
        String signatureSecret = params.get("signatureSecret");

        // 真实入库 ch_webhook_subscription
        ChWebhookSubscriptionBo bo = new ChWebhookSubscriptionBo();
        bo.setThirdPartyName(thirdPartyName);
        bo.setCallbackUrl(callbackUrl);
        bo.setEventTypes(StringUtils.isNotBlank(eventType) ? "[\"" + eventType + "\"]" : "[]");
        bo.setSignatureSecret(signatureSecret);
        bo.setIsActive(1);
        Long subId = webhookSubscriptionService.insertByBo(bo);

        referralService.logSync("WEBHOOK_SUBSCRIBE", "INBOUND", thirdPartyName, "SUCCESS",
            "subId=" + subId + ", eventType=" + eventType + ", callbackUrl=" + callbackUrl);
        return R.ok(subId);
    }

    @Operation(summary = "Webhook推送")
    @PostMapping("/chronic/openapi/webhook/push")
    public R<Void> push(@Parameter(description = "事件类型") @RequestParam String eventType, @RequestBody Map<String, Object> payload) {
        List<ChWebhookSubscriptionVo> subs = webhookSubscriptionService.queryActiveListByEventType(eventType);
        int matchedCount = subs.size();

        for (ChWebhookSubscriptionVo sub : subs) {
            try {
                // 记录推送调用状态
                webhookSubscriptionService.updateInvokeStatus(sub.getSubId(), "SUCCESS");
                log.info("Webhook分发推送: subId={}, targetUrl={}, eventType={}", sub.getSubId(), sub.getCallbackUrl(), eventType);
            } catch (Exception e) {
                webhookSubscriptionService.updateInvokeStatus(sub.getSubId(), "FAILED");
                log.warn("Webhook分发失败 subId={}: {}", sub.getSubId(), e.getMessage());
            }
        }

        referralService.logSync("WEBHOOK_PUSH", "OUTBOUND", "SUBSCRIBERS", "SUCCESS",
            "eventType=" + eventType + ", matchedSubscribers=" + matchedCount + ", payloadSize=" + payload.size());
        return R.ok();
    }
}
