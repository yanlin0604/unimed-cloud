package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单取消请求
 */
@Data
public class DhOrderCancelBo {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String reason;
}
