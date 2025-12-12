package org.dromara.dh.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.domain.dto.*;
import org.dromara.dh.service.IChatService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * 聊天服务实现类
 *
 * <p>负责与数字人聊天服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    @Qualifier("webRtcWebClient")
    private final WebClient webClient;

    /**
     * 发送文本消息给数字人
     *
     * @param request 文本交互请求
     * @return 交互响应
     */
    @Override
    public Mono<ChatResponse> sendTextMessage(ChatRequest request) {
        log.info("发送文本消息给数字人 - 会话ID: {}, 类型: {}, 内容: {}, 是否打断: {}", 
            request.getSessionid(), request.getType(), 
            request.getText().length() > 50 ? request.getText().substring(0, 50) + "..." : request.getText(),
            request.getInterrupt());

        return webClient.post()
            .uri("/human")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal -> 
                    log.warn("发送文本消息失败，正在重试 - 重试次数: {}, 错误: {}", 
                        retrySignal.totalRetries() + 1, retrySignal.failure().getMessage())))
            .doOnSuccess(response -> {
                if (response.getCode() != null && response.getCode() != 0) {
                    log.warn("文本消息发送返回错误 - 错误码: {}, 消息: {}", 
                        response.getCode(), response.getMsg());
                } else {
                    log.info("文本消息发送成功 - 会话ID: {}", request.getSessionid());
                }
            })
            .doOnError(error -> 
                log.error("发送文本消息失败 - 会话ID: {}, 错误: {}", 
                    request.getSessionid(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("数字人服务返回错误状态码: {} - 响应体: {}", 
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("发送文本消息失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("发送文本消息时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }

    /**
     * 打断数字人当前说话
     *
     * @param request 打断请求
     * @return 打断响应
     */
    @Override
    public Mono<ChatResponse> interruptTalk(InterruptRequest request) {
        log.info("打断数字人说话 - 会话ID: {}", request.getSessionid());

        return webClient.post()
            .uri("/interrupt_talk")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatResponse.class)
            .doOnSuccess(response -> {
                if (response.getCode() != null && response.getCode() != 0) {
                    log.warn("打断操作返回错误 - 错误码: {}, 消息: {}", 
                        response.getCode(), response.getMsg());
                } else {
                    log.info("打断操作成功 - 会话ID: {}", request.getSessionid());
                }
            })
            .doOnError(error -> 
                log.error("打断操作失败 - 会话ID: {}, 错误: {}", 
                    request.getSessionid(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("数字人服务返回错误状态码: {} - 响应体: {}", 
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("打断操作失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("打断操作时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }

    /**
     * 查询数字人是否正在说话
     *
     * @param request 说话状态查询请求
     * @return 说话状态响应
     */
    @Override
    public Mono<SpeakingStatusResponse> getSpeakingStatus(SpeakingStatusRequest request) {
        log.info("查询数字人说话状态 - 会话ID: {}", request.getSessionid());

        return webClient.post()
            .uri("/is_speaking")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(SpeakingStatusResponse.class)
            .doOnSuccess(response -> {
                if (response.getCode() != null && response.getCode() == 0 && response.getData() != null) {
                    log.info("查询说话状态成功 - 会话ID: {}, 是否说话: {}", 
                        request.getSessionid(), response.getData().getIsSpeaking());
                } else {
                    log.warn("查询说话状态返回错误 - 错误码: {}", response.getCode());
                }
            })
            .doOnError(error -> 
                log.error("查询说话状态失败 - 会话ID: {}, 错误: {}", 
                    request.getSessionid(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("数字人服务返回错误状态码: {} - 响应体: {}", 
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("查询说话状态失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("查询说话状态时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }
}