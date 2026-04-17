package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值工单审核通过对象
 */
@Data
public class DhTopupApproveBo {

    /**
     * 工单ID
     */
    @NotNull(message = "工单ID不能为空")
    private Long ticketId;

    /**
     * 实际到账金额
     */
    @NotNull(message = "实际到账金额不能为空")
    private BigDecimal actualAmount;

    /**
     * 备注
     */
    private String remark;
}
