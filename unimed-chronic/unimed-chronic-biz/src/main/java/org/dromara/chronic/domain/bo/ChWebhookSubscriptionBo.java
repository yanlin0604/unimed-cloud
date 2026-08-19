package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.chronic.domain.entity.ChWebhookSubscription;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * Webhook订阅业务对象 ch_webhook_subscription
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChWebhookSubscription.class, reverseConvertGenerate = false)
public class ChWebhookSubscriptionBo extends BaseEntity {

    /** 订阅ID */
    @NotNull(message = "订阅ID不能为空", groups = { EditGroup.class })
    private Long subId;

    /** 第三方系统名称 */
    @NotBlank(message = "第三方系统名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String thirdPartyName;

    /** 订阅事件列表 */
    private String eventTypes;

    /** 回调地址 */
    @NotBlank(message = "回调地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String callbackUrl;

    /** 签名密钥 */
    private String signatureSecret;

    /** 最大重试次数 */
    private Integer retryMax;

    /** 重试策略 */
    private String retryStrategy;

    /** 是否启用 */
    private Integer isActive;

    /** 最近推送时间 */
    private Date lastInvokeTime;

    /** 最近推送状态 */
    private String lastInvokeStatus;
}
