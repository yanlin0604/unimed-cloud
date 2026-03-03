package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 训练任务请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "训练任务请求")
public class VideoUploadTrainRequest {

    @Schema(description = "数字人ID", example = "7856875483760")
    @NotBlank(message = "数字人ID不能为空")
    private String digitalId;

    @Schema(description = "静默视频URL", example = "http://47.113.122.118:9000/digital-bucket/system/20251212/58c5690f-1925-464a-adbc-db791b6b9a7b.mp4")
    @NotBlank(message = "静默视频URL不能为空")
    private String silentVideoUrl;

    @Schema(description = "形象标题", example = "111ce")
    @NotBlank(message = "形象标题不能为空")
    private String figureTitle;

    @Schema(description = "是否强制重新训练", example = "false")
    @NotNull(message = "是否强制重新训练不能为空")
    private Boolean forceRetrain;

    @Schema(description = "训练类型 avatar：头像训练,action：动作训练", example = "avatar")
    @NotBlank(message = "训练类型不能为空")
    private String type;
}
