package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Webhook 注册/推送开放接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-Webhook")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiWebhookController {

    private final IChReferralService referralService;

    @Operation(summary = "注册Webhook订阅")
    @PostMapping("/chronic/openapi/webhook/subscribe")
    public R<Void> subscribe(@RequestBody Map<String, String> params) {
        String callbackUrl = params.get("callbackUrl");
        String eventType = params.get("eventType");
        referralService.logSync("WEBHOOK_SUBSCRIBE", "INBOUND", "EXTERNAL", "SUCCESS",
            "eventType=" + eventType + ", callbackUrl=" + callbackUrl);
        return R.ok();
    }

    @Operation(summary = "Webhook推送")
    @PostMapping("/chronic/openapi/webhook/push")
    public R<Void> push(@Parameter(description = "事件类型") @RequestParam String eventType, @RequestBody Map<String, Object> payload) {
        referralService.logSync("WEBHOOK_PUSH", "OUTBOUND", "EXTERNAL", "SUCCESS",
            "eventType=" + eventType + ", payloadSize=" + payload.size());
        return R.ok();
    }
}
