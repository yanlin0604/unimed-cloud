package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * WebRTC 连接请求 DTO
 *
 * <p>用于建立 WebRTC 连接的请求参数</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "WebRTC 连接请求")
public class WebRtcOfferRequest {

    @Schema(description = "WebRTC 会话描述协议数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SDP 数据不能为空")
    private String sdp;

    @Schema(description = "SDP 类型", example = "offer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SDP 类型不能为空")
    private String type;

    @Schema(description = "会话ID，如果不传则自动生成6位随机数", example = "123456")
    private String sessionid;
}