package org.dromara.dhcore.domain.bo;

import lombok.Data;

/**
 * 财务汇总查询对�? */
@Data
public class DhFinanceSummaryQueryBo {

    /**
     * 时间范围（today/week/month/custom�?     */
    private String range;

    /**
     * 开始时�?     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
