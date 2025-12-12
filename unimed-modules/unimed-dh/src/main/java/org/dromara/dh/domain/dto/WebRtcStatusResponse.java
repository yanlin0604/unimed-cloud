package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * WebRTC 连接状态响应 DTO
 *
 * <p>WebRTC 连接状态监控信息</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "WebRTC 连接状态响应")
public class WebRtcStatusResponse {

    @Schema(description = "活跃连接数")
    private Integer activeConnections;

    @Schema(description = "总会话数")
    private Integer totalSessions;

    @Schema(description = "各会话的连接统计")
    private Map<String, Object> connectionStats;

    @Schema(description = "重连尝试次数统计")
    private Map<String, Object> reconnectAttempts;
}