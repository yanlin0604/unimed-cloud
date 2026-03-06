package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 通知模板视图对象
 */
@Data
public class DhNotifyTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long id;

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
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
