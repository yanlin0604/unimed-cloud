package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户余额调整对象
 */
@Data
public class DhBalanceAdjustBo {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 调整金额
     */
    @NotNull(message = "调整金额不能为空")
    private BigDecimal amount;

    /**
     * 调整原因
     */
    @NotBlank(message = "调整原因不能为空")
    private String reason;
}
