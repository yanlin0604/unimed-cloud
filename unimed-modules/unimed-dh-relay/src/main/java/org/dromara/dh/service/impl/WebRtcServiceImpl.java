package org.dromara.dh.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.domain.dto.WebRtcStatusResponse;
import org.dromara.dh.domain.dto.WebRtcOfferRequest;
import org.dromara.dh.domain.dto.WebRtcOfferResponse;
import org.dromara.dh.service.IWebRtcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * WebRTC 服务实现类
 *
 * <p>负责与数字人 WebRTC 服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
public class WebRtcServiceImpl implements IWebRtcService {

    private final WebClient webClient;

    /**
     * 构造函数注入 WebClient 实例
     */
    public WebRtcServiceImpl(@Qualifier("webRtcWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 建立 WebRTC 连接
     *
     * @param request WebRTC 连接请求
     * @return WebRTC 连接响应
     */
    @Override
    public Mono<WebRtcOfferResponse> establishConnection(WebRtcOfferRequest request) {
        log.info("开始建立 WebRTC 连接 - 会话ID: {}, SDP类型: {}, SDP长度: {}, 形象: {}, 音色: {}, TTS: {}, LLM: {}/{}",
            request.getSessionid(), request.getType(),
            request.getSdp() != null ? request.getSdp().length() : 0,
            request.getAvatarId(), request.getRefFile(), request.getTts(),
            request.getLlmProvider(), request.getLlmModel());

        return webClient.post()
            .uri("/offer")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(WebRtcOfferResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("WebRTC 连接建立失败，正在重试 - 重试次数: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, retrySignal.failure().getMessage())))
            .doOnSuccess(response -> {
                if (response.getCode() != null && response.getCode() != 0) {
                    log.warn("WebRTC 连接建立返回错误 - 错误码: {}, 消息: {}, 建议: {}",
                        response.getCode(), response.getMsg(), response.getSuggestion());
                } else {
                    log.info("WebRTC 连接建立成功 - 会话ID: {}, SDP类型: {}, SDP长度: {}",
                        response.getSessionid(), response.getType(),
                        response.getSdp() != null ? response.getSdp().length() : 0);
                }
            })
            .doOnError(error ->
                log.error("WebRTC 连接建立失败 - 会话ID: {}, 错误: {}",
                    request.getSessionid(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                if (ex.getStatusCode().value() == 409) {
                    log.error("WebRTC 连接冲突 - 状态码: {} - 响应体: {}",
                        ex.getStatusCode(), ex.getResponseBodyAsString());
                    return new RuntimeException("WebRTC 连接冲突: " + ex.getResponseBodyAsString(), ex);
                } else {
                    log.error("WebRTC 服务返回错误状态码: {} - 响应体: {}",
                        ex.getStatusCode(), ex.getResponseBodyAsString());
                    return new RuntimeException("WebRTC 连接建立失败: " + ex.getMessage(), ex);
                }
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("WebRTC 连接建立时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }

    /**
     * 获取 WebRTC 连接状态
     *
     * @return 连接状态信息
     */
    @Override
    public Mono<WebRtcStatusResponse> getConnectionStatus() {
        log.info("获取 WebRTC 连接状态");

        return webClient.get()
            .uri("/webrtc/status")
            .retrieve()
            .bodyToMono(WebRtcStatusResponse.class)
            .doOnSuccess(response ->
                log.info("获取 WebRTC 连接状态成功 - 活跃连接: {}, 总会话: {}",
                    response.getActiveConnections(), response.getTotalSessions()))
            .doOnError(error ->
                log.error("获取 WebRTC 连接状态失败 - 错误: {}", error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("WebRTC 服务返回错误状态码: {} - 响应体: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("获取连接状态失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("获取连接状态时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }
}
