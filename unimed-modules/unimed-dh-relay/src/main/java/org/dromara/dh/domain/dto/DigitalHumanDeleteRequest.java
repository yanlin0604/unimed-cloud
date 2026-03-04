package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数字人删除请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人删除请求")
public class DigitalHumanDeleteRequest {

    @Schema(description = "数字人ID", example = "7856875483760", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数字人ID不能为空")
    private String digitalHumanId;
}