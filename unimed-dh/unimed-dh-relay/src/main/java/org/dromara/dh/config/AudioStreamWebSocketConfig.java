package org.dromara.dh.config;

import org.dromara.dh.handler.AudioStreamWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * 音频流 WebSocket 配置
 *
 * <p>注册 WebSocket 路由：{@code /api/v1/dh/external/audio_stream}</p>
 *
 *
 * @author unimed
 * @since 2.5.1
 */
@Configuration
@EnableWebSocket
public class AudioStreamWebSocketConfig implements WebSocketConfigurer {

    private final AudioStreamWebSocketHandler audioStreamWebSocketHandler;

    public AudioStreamWebSocketConfig(AudioStreamWebSocketHandler audioStreamWebSocketHandler) {
        this.audioStreamWebSocketHandler = audioStreamWebSocketHandler;
    }

    /**
     * 注册 WebSocket 处理器路由
     *
     * <p>允许所有来源（生产环境可按需收紧 {@code setAllowedOrigins}）</p>
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(audioStreamWebSocketHandler, "/api/v1/dh/external/audio_stream")
                .setAllowedOriginPatterns("*");
    }

    /**
     * 配置 WebSocket 容器参数
     *
     * <ul>
     *   <li>最大二进制消息缓冲：4MB（适配较大音频帧）</li>
     *   <li>最大文本消息缓冲：64KB</li>
     *   <li>空闲超时：60 秒</li>
     * </ul>
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        var container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(4 * 1024 * 1024);
        container.setMaxTextMessageBufferSize(64 * 1024);
        container.setMaxSessionIdleTimeout(60_000L);
        return container;
    }

}
