package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 财务汇总视图对�? */
@Data
public class DhFinanceSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 累计充�?     */
    private BigDecimal totalTopup;

    /**
     * 累计消费
     */
    private BigDecimal totalConsume;

    /**
     * 总余�?     */
    private BigDecimal totalBalance;

    /**
     * 累计退�?     */
    private BigDecimal totalRefund;

    /**
     * 趋势数据
     */
    private List<DhFinanceTrendVo> trend;
}
