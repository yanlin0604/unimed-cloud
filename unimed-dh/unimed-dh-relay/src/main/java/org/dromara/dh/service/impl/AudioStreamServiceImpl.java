package org.dromara.dh.service.impl;

import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.service.IAudioStreamService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

/**
 * 音频流服务实现
 *
 * <p>将客户端发来的 PCM float32 音频二进制帧及控制文本帧，
 * 通过 WebSocket 中转到后端数字人 Python 服务的 /audio_stream 接口。
 * 同时将后端推送的消息原样转发回客户端。</p>
 *
 * <p>使用 jakarta.websocket 原生 API 连接后端，兼容 Tomcat JSR-356 实现，无弃用问题。</p>
 *
 * <p>协议说明：</p>
 * <ul>
 *   <li>Binary 帧：raw PCM，float32，little-endian，16000Hz，单声道</li>
 *   <li>Text 帧（控制）：{"action":"interrupt"} / {"action":"end"} / {"action":"ping"}</li>
 * </ul>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
public class AudioStreamServiceImpl implements IAudioStreamService {

    private final String webRtcBaseUrl;

    public AudioStreamServiceImpl(@Qualifier("webRtcBaseUrl") String webRtcBaseUrl) {
        this.webRtcBaseUrl = webRtcBaseUrl;
    }

    @Override
    public void connectAndBridge(WebSocketSession clientSession, String sessionId) {
        String backendUri = buildBackendWsUri(sessionId);
        log.info("音频流建立后端连接 - 会话ID: {}, 后端URI: {}", sessionId, backendUri);

        // 1. 向客户端推送连接成功消息
        sendConnectedMsg(clientSession, sessionId);

        // 2. 使用 jakarta.websocket 原生 API 连接后端
        var latch = new CountDownLatch(1);
        var endpoint = new BackendEndpoint(clientSession, sessionId, latch);

        try {
            var container = ContainerProvider.getWebSocketContainer();
            var backendSession = container.connectToServer(endpoint, URI.create(backendUri));
            log.info("已连接后端音频流服务 - 会话ID: {}", sessionId);

            // 将后端 session 存入客户端 attributes，供处理器转发帧时使用
            clientSession.getAttributes().put("backendJakartaSession", backendSession);

            // 3. 等待任意一端关闭
            latch.await();
        } catch (Exception e) {
            log.error("音频流后端连接异常 - 会话ID: {}, 错误: {}", sessionId, e.getMessage(), e);
            closeQuietly(clientSession);
        }
    }

    /**
     * 向客户端推送 connected 消息
     */
    private void sendConnectedMsg(WebSocketSession clientSession, String sessionId) {
        String msg = "{\"code\":0,\"msg\":\"connected\",\"sessionid\":\"" + sessionId + "\"}";
        try {
            clientSession.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            log.warn("发送 connected 消息失败 - 会话ID: {}, 错误: {}", sessionId, e.getMessage());
        }
    }

    /**
     * 构造后端 WebSocket URI
     */
    private String buildBackendWsUri(String sessionId) {
        String wsBase = webRtcBaseUrl
                .replaceFirst("^https://", "wss://")
                .replaceFirst("^http://", "ws://");
        if (wsBase.endsWith("/")) {
            wsBase = wsBase.substring(0, wsBase.length() - 1);
        }
        return wsBase + "/audio_stream?sessionid=" + sessionId;
    }

    private void closeQuietly(WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * 后端 WebSocket Endpoint（jakarta.websocket）
     *
     * <p>负责：后端消息 → 客户端转发；连接关闭时通知 latch。</p>
     */
    @ClientEndpoint
    public static class BackendEndpoint {

        private final WebSocketSession clientSession;
        private final String sessionId;
        private final CountDownLatch latch;

        public BackendEndpoint(WebSocketSession clientSession, String sessionId, CountDownLatch latch) {
            this.clientSession = clientSession;
            this.sessionId = sessionId;
            this.latch = latch;
        }

        @OnOpen
        public void onOpen(Session session) {
            log.info("后端音频流 WebSocket 连接建立 - 会话ID: {}", sessionId);
        }

        /** 后端推送文本消息（如 pong）→ 转发到客户端 */
        @OnMessage
        public void onTextMessage(String message, Session session) {
            log.debug("后端文本消息 → 客户端 - 会话ID: {}, 内容: {}", sessionId, message);
            try {
                if (clientSession.isOpen()) {
                    clientSession.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.warn("转发后端文本消息失败 - 会话ID: {}, 错误: {}", sessionId, e.getMessage());
            }
        }

        /** 后端推送二进制消息 → 转发到客户端 */
        @OnMessage
        public void onBinaryMessage(ByteBuffer message, Session session) {
            log.debug("后端二进制消息 → 客户端 - 会话ID: {}, 大小: {} bytes", sessionId, message.remaining());
            try {
                if (clientSession.isOpen()) {
                    clientSession.sendMessage(
                            new org.springframework.web.socket.BinaryMessage(message));
                }
            } catch (IOException e) {
                log.warn("转发后端二进制消息失败 - 会话ID: {}, 错误: {}", sessionId, e.getMessage());
            }
        }

        @OnClose
        public void onClose(Session session, CloseReason closeReason) {
            log.info("后端音频流 WebSocket 连接关闭 - 会话ID: {}, 原因: {}", sessionId, closeReason);
            closeQuietly(clientSession);
            latch.countDown();
        }

        @OnError
        public void onError(Session session, Throwable throwable) {
            log.error("后端音频流传输错误 - 会话ID: {}, 错误: {}", sessionId, throwable.getMessage(), throwable);
            closeQuietly(clientSession);
            latch.countDown();
        }

        private void closeQuietly(WebSocketSession s) {
            try {
                if (s != null && s.isOpen()) {
                    s.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
