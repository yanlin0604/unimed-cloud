package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 通知模板提交对象
 */
@Data
public class DhNotifyTemplateBo {

    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    /**
     * 通知场景
     */
    @NotBlank(message = "通知场景不能为空")
    private String scene;

    /**
     * 通知渠道
     */
    @NotBlank(message = "通知渠道不能为空")
    private String channel;

    /**
     * 模板内容
     */
    @NotBlank(message = "模板内容不能为空")
    private String content;

    /**
     * 超时阈值（小时�?     */
    private Integer timeoutHours;

    /**
     * 状�?     */
    @NotBlank(message = "状态不能为�?)
    private String status;
}
