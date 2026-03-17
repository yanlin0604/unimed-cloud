package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 财务明细查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhFinanceDetailQueryBo extends BaseEntity {

    /**
     * 流水类型
     */
    private String type;

    /**
     * 关键�?     */
    private String keyword;

    /**
     * 开始时�?     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
