package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员配置提交对象
 */
@Data
public class DhMemberConfigBo {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 会员等级
     */
    @NotBlank(message = "会员等级不能为空")
    private String level;

    /**
     * 等级名称
     */
    @NotBlank(message = "等级名称不能为空")
    private String levelName;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal orderPrice;

    /**
     * 月度额度
     */
    @NotNull(message = "月度额度不能为空")
    private Integer monthlyLimit;

    /**
     * 速度优先�?     */
    @NotNull(message = "速度优先级不能为�?)
    private Integer speedPriority;

    /**
     * 最低充值要�?     */
    @NotNull(message = "最低充值要求不能为�?)
    private BigDecimal minTopupAmount;

    /**
     * 有效期天�?     */
    @NotNull(message = "有效期天数不能为�?)
    private Integer validityDays;

    /**
     * 预计交付时长
     */
    @NotNull(message = "预计交付时长不能为空")
    private Integer expectDeliveryHours;

    /**
     * 重做次数上限
     */
    @NotNull(message = "重做次数上限不能为空")
    private Integer redoLimit;

    /**
     * 状态（0启用 1停用�?     */
    @NotBlank(message = "状态不能为�?)
    private String status;

    /**
     * 备注
     */
    private String remark;
}
