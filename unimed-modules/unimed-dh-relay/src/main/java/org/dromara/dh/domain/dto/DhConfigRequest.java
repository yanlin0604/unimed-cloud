package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数字人配置请求 DTO
 *
 * <p>用于保存数字人配置的请求参数</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人配置请求")
public class DhConfigRequest {

    @Schema(description = "配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配置信息不能为空")
    @Valid
    private ConfigInfo configs;

    /**
     * 配置信息
     */
    @Data
    @Schema(description = "配置信息")
    public static class ConfigInfo {

        @Schema(description = "声音类型/参考文件", example = "BV700_V2_streaming", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "声音类型不能为空")
        private String refFile;

        @Schema(description = "数字人ID", example = "测试001_20251125_9763582602965_avatar", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "数字人ID不能为空")
        private String avatarId;
    }
}