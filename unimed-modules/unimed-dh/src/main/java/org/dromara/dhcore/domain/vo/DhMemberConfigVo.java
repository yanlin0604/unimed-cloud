package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员配置视图对象
 */
@Data
public class DhMemberConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 会员等级
     */
    private String level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 单价
     */
    private BigDecimal orderPrice;

    /**
     * 月度额度
     */
    private Integer monthlyLimit;

    /**
     * 速度优先�?     */
    private Integer speedPriority;

    /**
     * 最低充值要�?     */
    private BigDecimal minTopupAmount;

    /**
     * 有效期天�?     */
    private Integer validityDays;

    /**
     * 预计交付时长
     */
    private Integer expectDeliveryHours;

    /**
     * 重做次数上限
     */
    private Integer redoLimit;

    /**
     * 状�?     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
