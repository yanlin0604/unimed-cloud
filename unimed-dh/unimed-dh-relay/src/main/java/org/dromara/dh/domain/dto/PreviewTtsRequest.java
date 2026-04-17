package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TTS 音色试听请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "TTS 音色试听请求")
public class PreviewTtsRequest {

    @Schema(description = "TTS 类型，当前支持 doubao", example = "doubao")
    @NotBlank(message = "TTS 类型不能为空")
    private String ttsType;

    @Schema(description = "音色 ID", example = "zh_female_wanwanxiaohe_moon_bigtts")
    @NotBlank(message = "音色 ID 不能为空")
    private String voiceType;

    @Schema(description = "试听文本，默认值为：这是一个试听测试", example = "这是一个试听测试")
    private String text = "这是一个试听测试";
}
