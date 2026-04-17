package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * WebRTC 连接请求 DTO
 *
 * <p>用于建立 WebRTC 连接的请求参数。只有 sdp 和 type 是必填参数，
 * 其余均为可选的会话级配置，不传时自动回退到全局配置（向后兼容）。</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "WebRTC 连接请求")
public class WebRtcOfferRequest {

    // ==================== 必填参数 ====================

    @Schema(description = "WebRTC 会话描述协议数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SDP 数据不能为空")
    private String sdp;

    @Schema(description = "SDP 类型", example = "offer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SDP 类型不能为空")
    private String type;

    // ==================== 会话配置（可选） ====================

    @Schema(description = "会话ID，不传则自动生成6位随机数（建议用字符串以保留前导零）", example = "123456")
    private String sessionid;

    @Schema(description = "形象ID，对应 data/avatars 下的子目录名，不传使用全局配置",
        example = "测试001_20251125_9763582602965_avatar")
    @JsonProperty("avatar_id")
    private String avatarId;

    // ==================== TTS 配置（可选） ====================

    @Schema(description = "TTS 服务类型",
        allowableValues = {"edgetts", "gpt-sovits", "xtts", "cosyvoice", "fishtts", "tencent", "doubao", "indextts2"},
        example = "edgetts")
    private String tts;

    @Schema(description = "TTS 参考音频文件路径/声音名称，控制合成音色", example = "BV700_V2_streaming")
    @JsonProperty("REF_FILE")
    private String refFile;

    @Schema(description = "TTS 参考文本，部分 TTS 服务需配合 REF_FILE 使用")
    @JsonProperty("REF_TEXT")
    private String refText;

    @Schema(description = "TTS 服务器地址，自定义服务端点", example = "http://localhost:9880")
    @JsonProperty("TTS_SERVER")
    private String ttsServer;

    // ==================== LLM 配置（可选） ====================

    @Schema(description = "LLM 提供商",
        allowableValues = {"dashscope", "ollama", "maxkb", "unimed", "dify"},
        example = "dashscope")
    @JsonProperty("llm_provider")
    private String llmProvider;

    @Schema(description = "LLM 模型名称", example = "qwen2:0.5b")
    @JsonProperty("llm_model")
    private String llmModel;

    @Schema(description = "LLM 系统提示词，设置 AI 角色和行为")
    @JsonProperty("llm_system_prompt")
    private String llmSystemPrompt;

    @Schema(description = "LLM API 密钥（敏感信息）")
    @JsonProperty("llm_api_key")
    private String llmApiKey;

    @Schema(description = "Ollama 服务器地址，仅 llm_provider=ollama 时有效", example = "http://localhost:11434")
    @JsonProperty("ollama_host")
    private String ollamaHost;

    // ==================== 豆包 TTS 配置（可选） ====================

    @Schema(description = "豆包 TTS 应用ID，仅 tts=doubao 时需要")
    @JsonProperty("DOUBAO_APPID")
    private String doubaoAppId;

    @Schema(description = "豆包 TTS 令牌，仅 tts=doubao 时需要")
    @JsonProperty("DOUBAO_TOKEN")
    private String doubaoToken;

    @Schema(description = "豆包 TTS 音频参数，仅 tts=doubao 时有效")
    @JsonProperty("doubao_audio")
    @Valid
    private DoubaoAudio doubaoAudio;

    /**
     * 豆包 TTS 音频参数
     */
    @Data
    @Schema(description = "豆包 TTS 音频参数")
    public static class DoubaoAudio {

        @Schema(description = "语速比率", example = "1.0", minimum = "0.5", maximum = "2.0")
        @JsonProperty("speed_ratio")
        @DecimalMin(value = "0.5", message = "语速比率最小为 0.5")
        @DecimalMax(value = "2.0", message = "语速比率最大为 2.0")
        private Double speedRatio;

        @Schema(description = "音量比率", example = "1.0", minimum = "0.5", maximum = "2.0")
        @JsonProperty("volume_ratio")
        @DecimalMin(value = "0.5", message = "音量比率最小为 0.5")
        @DecimalMax(value = "2.0", message = "音量比率最大为 2.0")
        private Double volumeRatio;

        @Schema(description = "音调比率", example = "1.0", minimum = "0.5", maximum = "2.0")
        @JsonProperty("pitch_ratio")
        @DecimalMin(value = "0.5", message = "音调比率最小为 0.5")
        @DecimalMax(value = "2.0", message = "音调比率最大为 2.0")
        private Double pitchRatio;
    }
}
