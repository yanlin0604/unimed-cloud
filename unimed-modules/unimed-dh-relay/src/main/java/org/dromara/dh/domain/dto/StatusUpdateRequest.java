package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 状态修改请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "状态修改请求")
public class StatusUpdateRequest {

    @Schema(description = "数字人ID", example = "319")
    @NotNull(message = "数字人ID不能为空")
    private Long id;

    @Schema(description = "视频合成状态", example = "1")
    @NotNull(message = "视频合成状态不能为空")
    private Integer videoComposeState;

    @Schema(description = "合成视频URL", example = "http://47.113.122.118:9000/digital-bucket/system/20251212/58c5690f-1925-464a-adbc-db791b6b9a7b.mp4")
    @NotBlank(message = "合成视频URL不能为空")
    private String composeVideoUrl;

    @Schema(description = "训练人物ID", example = "111ce_20251212_9028904922969_avatar")
    @NotBlank(message = "训练人物ID不能为空")
    private String trainHumanId;
}