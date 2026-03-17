package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播审计日志对�?dh_audit_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_audit_log")
public class DhAuditLog extends TenantEntity {

    /**
     * 审计日志ID
     */
    @TableId(value = "log_id")
    private Long logId;

    /**
     * 审计动作
     */
    private String action;

    /**
     * 操作�?     */
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
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
