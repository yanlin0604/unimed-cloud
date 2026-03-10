package org.dromara.dh.service;

import org.dromara.dh.domain.dto.PreviewTtsRequest;
import org.dromara.dh.domain.dto.PreviewTtsResponse;
import org.dromara.dh.service.impl.DigitalHumanApiServiceImpl;
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
import static org.mockito.Mockito.when;

/**
 * TTS 音色试听服务测试
 *
 * @author unimed
 * @since 2.5.1
 */
@ExtendWith(MockitoExtension.class)
class PreviewTtsServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient digitalHumanListWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private DigitalHumanApiServiceImpl digitalHumanApiService;

    @Test
    @DisplayName("TTS 音色试听 - 成功场景")
    @SuppressWarnings("unchecked")
    void previewTts_Success() {
        // Given
        var request = new PreviewTtsRequest();
        request.setTtsType("doubao");
        request.setVoiceType("zh_female_wanwanxiaohe_moon_bigtts");
        request.setText("这是一个试听测试");

        var audioData = new PreviewTtsResponse.AudioData();
        audioData.setAudioBase64("SGVsbG8gV29ybGQ=");
        audioData.setAudioFormat("mp3");

        var backendResponse = new PreviewTtsResponse();
        backendResponse.setSuccess(true);
        backendResponse.setMessage("试听成功");
        backendResponse.setData(audioData);

        // Mock WebClient 调用链
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/preview_tts")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PreviewTtsResponse.class)).thenReturn(Mono.just(backendResponse));

        // When & Then
        StepVerifier.create(digitalHumanApiService.previewTts(request))
            .expectNextMatches(response ->
                Boolean.TRUE.equals(response.getSuccess()) &&
                "试听成功".equals(response.getMessage()) &&
                response.getData() != null &&
                "SGVsbG8gV29ybGQ=".equals(response.getData().getAudioBase64()) &&
                "mp3".equals(response.getData().getAudioFormat()))
            .verifyComplete();
    }

    @Test
    @DisplayName("TTS 音色试听 - 失败场景（Python 返回 success=false）")
    @SuppressWarnings("unchecked")
    void previewTts_Failure() {
        // Given
        var request = new PreviewTtsRequest();
        request.setTtsType("doubao");
        request.setVoiceType("invalid_voice_type");
        request.setText("这是一个试听测试");

        var backendResponse = new PreviewTtsResponse();
        backendResponse.setSuccess(false);
        backendResponse.setMessage("音色ID不存在");

        // Mock WebClient 调用链
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/preview_tts")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PreviewTtsResponse.class)).thenReturn(Mono.just(backendResponse));

        // When & Then
        StepVerifier.create(digitalHumanApiService.previewTts(request))
            .expectNextMatches(response ->
                Boolean.FALSE.equals(response.getSuccess()) &&
                "音色ID不存在".equals(response.getMessage()) &&
                response.getData() == null)
            .verifyComplete();
    }

    @Test
    @DisplayName("TTS 音色试听 - 网络异常场景")
    @SuppressWarnings("unchecked")
    void previewTts_NetworkError() {
        // Given
        var request = new PreviewTtsRequest();
        request.setTtsType("doubao");
        request.setVoiceType("zh_female_wanwanxiaohe_moon_bigtts");
        request.setText("这是一个试听测试");

        // Mock 网络异常
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/preview_tts")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PreviewTtsResponse.class))
            .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        // When & Then
        StepVerifier.create(digitalHumanApiService.previewTts(request))
            .expectErrorMatches(throwable ->
                throwable instanceof RuntimeException &&
                throwable.getMessage().contains("Connection refused"))
            .verify();
    }
}
