package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端充值档位视图对象（对应 DhPaymentPriceConfig，只读）
 *
 * @author unimed
 */
@Data
public class PortalTopupPlanVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 赠送金额
     */
    private BigDecimal bonusAmount;

    /**
     * 排序号
     */
    private Integer sort;
}
