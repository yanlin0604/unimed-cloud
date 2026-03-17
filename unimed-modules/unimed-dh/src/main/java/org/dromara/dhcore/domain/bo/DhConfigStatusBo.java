package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 配置状态变更对�? */
@Data
public class DhConfigStatusBo {

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空")
    private Long id;

    /**
     * 配置状态（0启用 1停用�?     */
    @NotBlank(message = "配置状态不能为�?)
    private String status;
}
