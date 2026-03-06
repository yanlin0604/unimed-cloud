package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 数字人口播举报工单对象 dh_report_ticket
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_report_ticket")
public class DhReportTicket extends TenantEntity {

    /**
     * 举报单ID
     */
    @TableId(value = "report_id")
    private Long reportId;

    /**
     * 举报人
     */
    private String reporterName;

    /**
     * 被举报用户ID
     */
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
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
