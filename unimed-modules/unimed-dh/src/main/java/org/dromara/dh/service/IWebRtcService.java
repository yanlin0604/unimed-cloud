package org.dromara.dh.service;

import org.dromara.dh.domain.dto.WebRtcOfferRequest;
import org.dromara.dh.domain.dto.WebRtcOfferResponse;
import org.dromara.dh.domain.dto.WebRtcStatusResponse;
import reactor.core.publisher.Mono;

/**
 * WebRTC 服务接口
 *
 * <p>负责与数字人 WebRTC 服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface IWebRtcService {

    /**
     * 建立 WebRTC 连接
     *
     * @param request WebRTC 连接请求
     * @return WebRTC 连接响应
     */
    Mono<WebRtcOfferResponse> establishConnection(WebRtcOfferRequest request);

    /**
     * 获取 WebRTC 连接状态
     *
     * @return 连接状态信息
     */
    Mono<WebRtcStatusResponse> getConnectionStatus();
}