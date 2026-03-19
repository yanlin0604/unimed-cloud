package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 举报工单视图对象
 */
@Data
public class DhReportItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 举报单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 举报人
     */
    private String reporterName;

    /**
     * 被举报用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetUserId;

    /**
     * 被举报用户名
     */
    private String targetUserName;

    /**
     * 被举报内容ID
     */
    private String targetContentId;

    /**
     * 被举报内容类型
     */
    private String targetContentType;

    /**
     * 举报类型
     */
    private String type;

    /**
     * 举报描述
     */
    private String description;

    /**
     * 处理状态
     */
    private String status;

    /**
     * 处理人
     */
    private String handlerName;

    /**
     * 处理结论
     */
    private String handleResult;

    /**
     * 处理时间
     */
    private Date handleTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
