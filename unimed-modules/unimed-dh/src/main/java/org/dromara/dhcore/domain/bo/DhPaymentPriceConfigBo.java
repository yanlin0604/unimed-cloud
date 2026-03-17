package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值档位配置提交对�? */
@Data
public class DhPaymentPriceConfigBo {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    /**
     * 适用会员等级
     */
    @NotBlank(message = "适用会员等级不能为空")
    private String memberLevel;

    /**
     * 支付类型
     */
    @NotBlank(message = "支付类型不能为空")
    private String payType;

    /**
     * 充值金�?     */
    @NotNull(message = "充值金额不能为�?)
    private BigDecimal amount;

    /**
     * 赠送金�?     */
    @NotNull(message = "赠送金额不能为�?)
    private BigDecimal bonusAmount;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 状�?     */
    @NotBlank(message = "状态不能为�?)
    private String status;

    /**
     * 备注
     */
    private String remark;
}
