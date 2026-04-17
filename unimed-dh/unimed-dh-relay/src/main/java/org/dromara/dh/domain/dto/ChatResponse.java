package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文本交互响应 DTO
 *
 * <p>文本交互的响应结果</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "文本交互响应")
public class ChatResponse {

    @Schema(description = "状态码", example = "0")
    private Integer code;

    @Schema(description = "状态消息", example = "ok")
    private String msg;
}