package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值档位配置视图对�? */
@Data
public class DhPaymentPriceConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 适用会员等级
     */
    private String memberLevel;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 充值金�?     */
    private BigDecimal amount;

    /**
     * 赠送金�?     */
    private BigDecimal bonusAmount;

    /**
     * 排序
     */
    private Integer sort;

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
