package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单拒绝请求
 */
@Data
public class DhOrderRejectBo {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 违规类型
     */
    @NotBlank(message = "违规类型不能为空")
    private String violationType;

    /**
     * 驳回原因
     */
    @NotBlank(message = "拒绝原因不能为空")
    private String reason;
}
