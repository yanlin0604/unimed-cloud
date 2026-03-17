package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 审计日志查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhAuditLogQueryBo extends BaseEntity {

    /**
     * 审计动作
     */
    private String action;

    /**
     * 操作�?     */
    private String operatorName;

    /**
     * 开始时�?     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
