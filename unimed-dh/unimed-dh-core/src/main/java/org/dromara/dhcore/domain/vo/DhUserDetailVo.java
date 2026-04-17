package org.dromara.dhcore.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户详情视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhUserDetailVo extends DhUserItemVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 距离下一会员等级所需充值差额
     */
    private BigDecimal nextLevelGap;

    /**
     * 最近订单
     */
    private List<DhOrderItemVo> recentOrders;
}
