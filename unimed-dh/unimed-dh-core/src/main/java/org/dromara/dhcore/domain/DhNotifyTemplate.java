package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播通知模板对象 dh_notify_template
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_notify_template")
public class DhNotifyTemplate extends TenantEntity {

    /**
     * 通知模板ID
     */
    @TableId(value = "template_id")
    private Long templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 通知场景
     */
    private String scene;

    /**
     * 通知渠道
     */
    private String channel;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 超时阈值（小时）
     */
    private Integer timeoutHours;

    /**
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
