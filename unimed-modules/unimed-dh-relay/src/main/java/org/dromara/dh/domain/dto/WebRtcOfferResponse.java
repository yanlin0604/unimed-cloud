package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * WebRTC 连接响应 DTO
 *
 * <p>WebRTC 连接建立的响应结果</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "WebRTC 连接响应")
public class WebRtcOfferResponse {

    @Schema(description = "服务端的 SDP answer")
    private String sdp;

    @Schema(description = "SDP 类型", example = "answer")
    private String type;

    @Schema(description = "分配的会话ID")
    private String sessionid;

    @Schema(description = "错误码，成功时为空")
    private Integer code;

    @Schema(description = "错误消息，成功时为空")
    private String msg;

    @Schema(description = "建议信息，出现冲突时提供")
    private String suggestion;
}