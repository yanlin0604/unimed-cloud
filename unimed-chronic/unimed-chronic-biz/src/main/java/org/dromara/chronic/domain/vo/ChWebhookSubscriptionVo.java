package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChWebhookSubscription;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Webhook订阅视图对象 ch_webhook_subscription
 *
 * @author unimed
 */
@Data
@Schema(description = "Webhook订阅视图对象")
@AutoMapper(target = ChWebhookSubscription.class)
public class ChWebhookSubscriptionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 订阅ID */
    @Schema(description = "订阅ID")
    private Long subId;

    /** 第三方系统名称 */
    @Schema(description = "第三方系统名称")
    private String thirdPartyName;

    /** 订阅事件列表 */
    @Schema(description = "订阅事件列表")
    private String eventTypes;

    /** 回调地址 */
    @Schema(description = "回调地址")
    private String callbackUrl;

    /** 签名密钥 */
    @Schema(description = "签名密钥")
    private String signatureSecret;

    /** 最大重试次数 */
    @Schema(description = "最大重试次数")
    private Integer retryMax;

    /** 重试策略 */
    @Schema(description = "重试策略")
    private String retryStrategy;

    /** 是否启用 (1:启用, 0:停用) */
    @Schema(description = "是否启用")
    private Integer isActive;

    /** 最近推送时间 */
    @Schema(description = "最近推送时间")
    private Date lastInvokeTime;

    /** 最近推送状态 */
    @Schema(description = "最近推送状态")
    private String lastInvokeStatus;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createTime;
}
