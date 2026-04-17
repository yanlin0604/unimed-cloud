package org.dromara.dh.service;

import org.springframework.web.socket.WebSocketSession;

/**
 * 音频流服务接口
 *
 * <p>负责将客户端的 WebSocket 音频流中转到后端数字人 Python 服务</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface IAudioStreamService {

    /**
     * 建立到后端 Python /audio_stream 的 WebSocket 连接，并与客户端会话双向桥接
     *
     * @param clientSession 客户端 WebSocket 会话
     * @param sessionId     数字人会话 ID（与 /human 等接口一致）
     */
    void connectAndBridge(WebSocketSession clientSession, String sessionId);
}
