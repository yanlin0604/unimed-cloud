package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * WebRTC 连接状态响应 DTO
 * <p>WebRTC 连接状态监控信息</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "WebRTC 连接状态响应")
public class WebRtcStatusResponse {

    @Schema(description = "活跃连接数")
    @JsonProperty("active_connections")
    private Integer activeConnections;

    @Schema(description = "总会话数")
    @JsonProperty("total_sessions")
    private Integer totalSessions;

    @Schema(description = "各会话的连接统计")
    @JsonProperty("connection_stats")
    private Map<String, Object> connectionStats;

    @Schema(description = "重连尝试次数统计")
    @JsonProperty("reconnect_attempts")
    private Map<String, Object> reconnectAttempts;
}
