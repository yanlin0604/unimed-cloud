package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 举报查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhReportQueryBo extends BaseEntity {

    /**
     * 关键�?     */
    private String keyword;

    /**
     * 举报类型
     */
    private String type;

    /**
     * 处理状�?     */
    private String status;

    /**
     * 开始时�?     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
