package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 审计日志视图对象
 */
@Data
public class DhAuditLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 审计动作
     */
    private String action;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 详情
     */
    private String detail;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
