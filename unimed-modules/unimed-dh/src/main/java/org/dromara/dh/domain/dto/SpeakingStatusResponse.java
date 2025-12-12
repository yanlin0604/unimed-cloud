package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 说话状态响应 DTO
 *
 * <p>数字人说话状态的响应结果</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "说话状态响应")
public class SpeakingStatusResponse {

    @Schema(description = "状态码", example = "0")
    private Integer code;

    @Schema(description = "状态数据")
    private SpeakingData data;

    /**
     * 说话状态数据
     */
    @Data
    @Schema(description = "说话状态数据")
    public static class SpeakingData {

        @Schema(description = "是否正在说话", example = "true")
        private Boolean isSpeaking;

        @Schema(description = "当前音频类型")
        private String currentAudiotype;

        @Schema(description = "默认静音音频类型")
        private String defaultSilentAudiotype;

        @Schema(description = "可用的音频类型列表")
        private List<String> availableAudioTypes;
    }
}