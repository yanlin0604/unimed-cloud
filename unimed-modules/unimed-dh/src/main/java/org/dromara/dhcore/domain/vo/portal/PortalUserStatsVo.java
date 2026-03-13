package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * C端用户统计数据视图对象
 *
 * @author AI
 */
@Data
public class PortalUserStatsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总订单数
     */
    private Integer orderCount;

    /**
     * 已完成订单数
     */
    private Integer completedCount;

    /**
     * 累计消费金额
     */
    private BigDecimal totalConsume;

    /**
     * 累计充值金额
     */
    private BigDecimal totalTopup;

    /**
     * 作品数量
     */
    private Integer worksCount;
}
