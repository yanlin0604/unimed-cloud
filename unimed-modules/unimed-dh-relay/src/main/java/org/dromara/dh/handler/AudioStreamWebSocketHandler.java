package org.dromara.dh.handler;

import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.service.IAudioStreamService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 音频流 WebSocket 处理器
 *
 * <p>处理客户端 WebSocket 连接，双向桥接到后端数字人 Python 服务的 /audio_stream 接口。</p>
 *
 * <p>连接地址：{@code ws://host:9205/api/v1/dh/external/audio_stream?sessionid=0}</p>
 *
 * <p>支持的帧类型：</p>
 * <ul>
 *   <li>Binary：raw PCM float32 little-endian 16000Hz 单声道音频数据</li>
 *   <li>Text：控制指令 JSON，如 {"action":"interrupt"} / {"action":"end"} / {"action":"ping"}</li>
 * </ul>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AudioStreamWebSocketHandler extends AbstractWebSocketHandler {

    private final IAudioStreamService audioStreamService;

    /**
     * 连接建立后，在虚拟线程中发起后端桥接，避免阻塞 WebSocket I/O 线程
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = parseSessionId(session);
        log.info("音频流客户端连接建立 - 客户端会话: {}, sessionId: {}", session.getId(), sessionId);
        session.getAttributes().put("sessionId", sessionId);
        Thread.ofVirtual().name("audio-stream-" + sessionId)
                .start(() -> audioStreamService.connectAndBridge(session, sessionId));
    }

    /**
     * 收到客户端二进制帧（PCM 音频数据）→ 转发到后端
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        Session backendSession = getBackendJakartaSession(session);
        if (backendSession != null && backendSession.isOpen()) {
            log.debug("客户端音频帧 → 后端 - 会话ID: {}, 大小: {} bytes",
                    session.getAttributes().get("sessionId"), message.getPayloadLength());
            try {
                backendSession.getBasicRemote().sendBinary(
                        ByteBuffer.wrap(message.getPayload().array()));
            } catch (IOException e) {
                log.warn("转发音频帧到后端失败 - 会话ID: {}, 错误: {}",
                        session.getAttributes().get("sessionId"), e.getMessage());
            }
        } else {
            log.warn("后端会话未就绪，丢弃音频帧 - 会话ID: {}", session.getAttributes().get("sessionId"));
        }
    }

    /**
     * 收到客户端文本帧（控制指令）→ 转发到后端
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Session backendSession = getBackendJakartaSession(session);
        if (backendSession != null && backendSession.isOpen()) {
            log.debug("客户端控制指令 → 后端 - 会话ID: {}, 内容: {}",
                    session.getAttributes().get("sessionId"), message.getPayload());
            try {
                backendSession.getBasicRemote().sendText(message.getPayload());
            } catch (IOException e) {
                log.warn("转发控制指令到后端失败 - 会话ID: {}, 错误: {}",
                        session.getAttributes().get("sessionId"), e.getMessage());
            }
        } else {
            log.warn("后端会话未就绪，丢弃控制指令 - 会话ID: {}, 指令: {}",
                    session.getAttributes().get("sessionId"), message.getPayload());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("音频流客户端连接关闭 - 会话ID: {}, 状态: {}",
                session.getAttributes().get("sessionId"), status);
        Session backendSession = getBackendJakartaSession(session);
        if (backendSession != null && backendSession.isOpen()) {
            try {
                backendSession.close();
            } catch (IOException e) {
                log.warn("关闭后端会话异常: {}", e.getMessage());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("音频流客户端传输错误 - 会话ID: {}, 错误: {}",
                session.getAttributes().get("sessionId"), exception.getMessage(), exception);
    }

    /**
     * 从 session attributes 中获取后端 jakarta.websocket.Session
     */
    private Session getBackendJakartaSession(WebSocketSession session) {
        Object obj = session.getAttributes().get("backendJakartaSession");
        if (obj instanceof Session jakartaSession) {
            return jakartaSession;
        }
        return null;
    }

    /**
     * 从 URL 查询参数中解析 sessionid，默认返回 "0"
     */
    private String parseSessionId(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query == null || query.isBlank()) {
            return "0";
        }
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && "sessionid".equals(kv[0].trim())) {
                return kv[1].trim();
            }
        }
        return "0";
    }
}
