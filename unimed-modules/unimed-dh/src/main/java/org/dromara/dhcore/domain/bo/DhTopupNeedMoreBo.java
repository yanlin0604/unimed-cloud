package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 充值工单补充材料对象
 */
@Data
public class DhTopupNeedMoreBo {

    /**
     * 工单ID
     */
    @NotNull(message = "工单ID不能为空")
    private Long ticketId;

    /**
     * 补充原因
     */
    @NotBlank(message = "补充原因不能为空")
    private String reason;
}
