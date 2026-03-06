package org.dromara.dhcore.domain.bo;

import lombok.Data;

/**
 * 财务汇总查询对象
 */
@Data
public class DhFinanceSummaryQueryBo {

    /**
     * 时间范围（today/week/month/custom）
     */
    private String range;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
