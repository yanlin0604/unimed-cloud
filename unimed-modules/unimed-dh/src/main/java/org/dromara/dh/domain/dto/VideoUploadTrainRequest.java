package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 视频上传和训练请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "视频上传和训练请求")
public class VideoUploadTrainRequest {

    @Schema(description = "静默视频URL", example = "http://47.113.122.118:9000/digital-bucket/system/20251212/58c5690f-1925-464a-adbc-db791b6b9a7b.mp4")
    @NotBlank(message = "静默视频URL不能为空")
    private String silentVideoUrl;

    @Schema(description = "动作视频URL", example = "http://example.com/action.mp4")
    private String actionVideoUrl;

    @Schema(description = "形象标题", example = "111ce")
    @NotBlank(message = "形象标题不能为空")
    private String figureTitle;

    @Schema(description = "性别", example = "male", allowableValues = {"male", "female"})
    @NotBlank(message = "性别不能为空")
    private String sex;

    @Schema(description = "形象介绍", example = "这是一个数字人形象")
    private String figureIntroduction;

    @Schema(description = "是否更换背景", example = "false")
    @NotNull(message = "是否更换背景不能为空")
    private Boolean changeBackground;

    @Schema(description = "替换背景URL", example = "http://example.com/bg.jpg")
    private String replaceBg;

    @Schema(description = "声音文件", example = "BV705_streaming")
    @NotBlank(message = "声音文件不能为空")
    private String voiceFile;

    @Schema(description = "是否同意协议", example = "false")
    @NotNull(message = "是否同意协议不能为空")
    private Boolean agree;

    @Schema(description = "是否强制重新训练", example = "false")
    @NotNull(message = "是否强制重新训练不能为空")
    private Boolean forceRetrain;

    @Schema(description = "训练类型", example = "avatar")
    @NotBlank(message = "训练类型不能为空")
    private String type;
}