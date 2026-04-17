package org.dromara.dh.service;

import org.dromara.dh.domain.dto.*;
import reactor.core.publisher.Mono;

/**
 * 聊天服务接口
 *
 * <p>负责与数字人聊天服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface IChatService {

    /**
     * 发送文本消息给数字人
     *
     * @param request 文本交互请求
     * @return 交互响应
     */
    Mono<ChatResponse> sendTextMessage(ChatRequest request);

    /**
     * 打断数字人当前说话
     *
     * @param request 打断请求
     * @return 打断响应
     */
    Mono<ChatResponse> interruptTalk(InterruptRequest request);

    /**
     * 查询数字人是否正在说话
     *
     * @param request 说话状态查询请求
     * @return 说话状态响应
     */
    Mono<SpeakingStatusResponse> getSpeakingStatus(SpeakingStatusRequest request);
}