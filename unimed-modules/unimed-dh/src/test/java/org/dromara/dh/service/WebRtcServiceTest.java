package org.dromara.dh.service;

import org.dromara.dh.service.impl.WebRtcServiceImpl;

import org.dromara.dh.domain.dto.WebRtcStatusResponse;
import org.dromara.dh.domain.dto.WebRtcOfferRequest;
import org.dromara.dh.domain.dto.WebRtcOfferResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * WebRtcService 测试类
 *
 * @author unimed
 * @since 2.5.1
 */
@ExtendWith(MockitoExtension.class)
class WebRtcServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private WebRtcServiceImpl webRtcService;

    @Test
    @DisplayName("建立 WebRTC 连接 - 成功场景")
    void establishConnection_Success() {
        // Given
        var request = new WebRtcOfferRequest();
        request.setSdp("test-sdp-data");
        request.setType("offer");
        request.setSessionid("123456");

        var response = new WebRtcOfferResponse();
        response.setSdp("test-answer-sdp");
        response.setType("answer");
        response.setSessionid("123456");

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WebRtcOfferResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(webRtcService.establishConnection(request))
            .expectNext(response)
            .verifyComplete();
    }

    @Test
    @DisplayName("获取连接状态 - 成功场景")
    void getConnectionStatus_Success() {
        // Given
        var response = new WebRtcStatusResponse();
        response.setActiveConnections(5);
        response.setTotalSessions(10);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WebRtcStatusResponse.class)).thenReturn(Mono.just(response));

        // When & Then
        StepVerifier.create(webRtcService.getConnectionStatus())
            .expectNext(response)
            .verifyComplete();
    }
}