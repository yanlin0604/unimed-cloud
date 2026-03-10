package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * TTS 音色试听响应
 *
 * <p>对应 Python 后端 /preview_tts 接口返回格式：
 * <pre>
 * 成功：{ "success": true, "message": "试听成功", "data": { "audio_base64": "...", "audio_format": "mp3" } }
 * 失败：{ "success": false, "message": "错误描述" }
 * </pre>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "TTS 音色试听响应")
public class PreviewTtsResponse {

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "响应消息", example = "试听成功")
    private String message;

    @Schema(description = "音频数据（成功时有值）")
    private AudioData data;

    /**
     * 音频数据
     */
    @Data
    @Schema(description = "音频数据")
    public static class AudioData {

        @Schema(description = "base64 编码的音频内容（MP3格式）")
        @JsonProperty("audio_base64")
        private String audioBase64;

        @Schema(description = "音频格式", example = "mp3")
        @JsonProperty("audio_format")
        private String audioFormat;
    }
}
