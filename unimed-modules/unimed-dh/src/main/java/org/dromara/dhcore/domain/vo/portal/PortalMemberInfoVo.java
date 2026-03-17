package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端当前用户会员信息视图对�?
 *
 * @author unimed
 */
@Data
public class PortalMemberInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会员等级（NORMAL/VIP/SVIP�?
     */
    private String level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 到期时间
     */
    private Date expireTime;

    /**
     * 单次订单价格
     */
    private BigDecimal orderPrice;

    /**
     * 月度下单限额
     */
    private Integer monthlyLimit;

    /**
     * 速度优先�?
     */
    private Integer speedPriority;

    /**
     * 预期交付时长（小时）
     */
    private Integer expectDeliveryHours;

    /**
     * 返工次数上限
     */
    private Integer redoLimit;
}
