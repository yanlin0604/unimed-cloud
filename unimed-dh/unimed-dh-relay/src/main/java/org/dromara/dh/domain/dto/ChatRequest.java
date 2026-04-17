package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文本交互请求 DTO
 *
 * <p>用于发送文本消息给数字人的请求参数</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "文本交互请求")
public class ChatRequest {

    @Schema(description = "要发送的文本内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文本内容不能为空")
    private String text;

    @Schema(description = "消息类型", example = "echo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息类型不能为空")
    private ChatType type;

    @Schema(description = "是否打断当前说话", example = "false")
    private Boolean interrupt = false;

    @Schema(description = "会话ID", example = "123456")
    private String sessionid = "0";

    /**
     * 消息类型枚举
     */
    public enum ChatType {
        @Schema(description = "直接播报模式")
        echo,
        
        @Schema(description = "AI对话模式")
        chat
    }
}