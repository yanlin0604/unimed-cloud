package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * C端会员等级配置视图对象（用于权益对比表）
 *
 * @author unimed
 */
@Data
public class PortalMemberLevelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 会员等级（NORMAL/VIP/SVIP�?
     */
    private String level;

    /**
     * 等级名称
     */
    private String levelName;

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
     * 最低充值金额要�?
     */
    private BigDecimal minTopupAmount;

    /**
     * 有效期（天）
     */
    private Integer validityDays;

    /**
     * 预期交付时长（小时）
     */
    private Integer expectDeliveryHours;

    /**
     * 返工次数上限
     */
    private Integer redoLimit;
}
