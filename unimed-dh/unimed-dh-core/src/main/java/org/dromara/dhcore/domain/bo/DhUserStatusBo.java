package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态变更对象
 */
@Data
public class DhUserStatusBo {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户状态（0启用 1停用）
     */
    @NotBlank(message = "用户状态不能为空")
    private String status;
}
