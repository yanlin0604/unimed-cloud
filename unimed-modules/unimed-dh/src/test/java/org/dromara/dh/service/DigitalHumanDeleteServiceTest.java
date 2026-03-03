package org.dromara.dh.service;

import org.dromara.dh.domain.dto.DigitalHumanDeleteResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 数字人删除服务测试
 *
 * @author unimed
 * @since 2.5.1
 */
@ExtendWith(MockitoExtension.class)
class DigitalHumanDeleteServiceTest {

    @Mock
    private WebClient digitalHumanListWebClient;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private DigitalHumanApiServiceImpl digitalHumanApiService;

    @Test
    @DisplayName("删除数字人 - 成功场景")
    void deleteDigitalHuman_Success() {
        // Given
        var digitalHumanId = "7856875483760";

        var digitalServiceResult = new DigitalHumanDeleteResponse.DigitalServiceDeleteResult();
        digitalServiceResult.setCode(200);
        digitalServiceResult.setMsg("操作成功");

        var trainingServiceResult = new DigitalHumanDeleteResponse.TrainingServiceDeleteResult();
        trainingServiceResult.setSuccess(true);
        trainingServiceResult.setMessage("任务已删除");
        trainingServiceResult.setTaskId("4486675750304");

        // Mock 数字人服务删除
        when(digitalHumanListWebClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DigitalHumanDeleteResponse.DigitalServiceDeleteResult.class))
            .thenReturn(Mono.just(digitalServiceResult));

        // Mock 训练服务删除
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);

        // When & Then
        StepVerifier.create(digitalHumanApiService.deleteDigitalHuman(digitalHumanId))
            .expectNextMatches(response -> 
                response.getSuccess() != null && 
                response.getSuccess() && 
                "数字人删除成功".equals(response.getMessage()))
            .verifyComplete();
    }

    @Test
    @DisplayName("删除数字人 - 部分失败场景")
    void deleteDigitalHuman_PartialFailure() {
        // Given
        var digitalHumanId = "7856875483760";

        var digitalServiceResult = new DigitalHumanDeleteResponse.DigitalServiceDeleteResult();
        digitalServiceResult.setCode(500);
        digitalServiceResult.setMsg("服务器错误");

        var trainingServiceResult = new DigitalHumanDeleteResponse.TrainingServiceDeleteResult();
        trainingServiceResult.setSuccess(true);
        trainingServiceResult.setMessage("任务已删除");

        // Mock responses
        when(digitalHumanListWebClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DigitalHumanDeleteResponse.DigitalServiceDeleteResult.class))
            .thenReturn(Mono.just(digitalServiceResult));

        when(webClient.delete()).thenReturn(requestHeadersUriSpec);

        // When & Then
        StepVerifier.create(digitalHumanApiService.deleteDigitalHuman(digitalHumanId))
            .expectNextMatches(response -> 
                response.getSuccess() != null && 
                !response.getSuccess() && 
                response.getMessage().contains("数字人删除部分失败"))
            .verifyComplete();
    }
}