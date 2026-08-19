package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * Webhook订阅对象 ch_webhook_subscription
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_webhook_subscription")
public class ChWebhookSubscription extends TenantEntity {

    /** 订阅ID */
    @TableId(value = "sub_id")
    private Long subId;

    /** 第三方系统名称 */
    private String thirdPartyName;

    /** 订阅事件列表(如["WARNING_CREATED", "FOLLOWUP_DONE"]) */
    private String eventTypes;

    /** 回调地址 */
    private String callbackUrl;

    /** 签名密钥(HMAC 签名校验) */
    private String signatureSecret;

    /** 最大重试次数 */
    private Integer retryMax;

    /** 重试策略(EXPONENTIAL_BACKOFF/LINEAR) */
    private String retryStrategy;

    /** 是否启用 (1:启用, 0:停用) */
    private Integer isActive;

    /** 最近推送时间 */
    private Date lastInvokeTime;

    /** 最近推送状态 */
    private String lastInvokeStatus;

    /** 删除标志(0存在 1删除) */
    @TableLogic
    private String delFlag;
}
