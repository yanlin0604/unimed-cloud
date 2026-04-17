package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 充值工单驳回对象
 */
@Data
public class DhTopupRejectBo {

    /**
     * 工单ID
     */
    @NotNull(message = "工单ID不能为空")
    private Long ticketId;

    /**
     * 驳回原因
     */
    @NotBlank(message = "驳回原因不能为空")
    private String reason;
}
