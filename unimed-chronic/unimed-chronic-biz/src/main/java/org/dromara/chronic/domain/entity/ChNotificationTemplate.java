package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 通知模板对象 ch_notification_template
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_notification_template")
public class ChNotificationTemplate extends TenantEntity {

    @TableId(value = "template_id")
    private Long templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 通道: WECHAT/SMS/IN_APP/IVR
     */
    private String channel;

    private String templateCode;

    private String templateContent;

    /**
     * 是否启用: 1启用 0停用
     */
    private String isActive;

    @TableLogic
    private String delFlag;
}
