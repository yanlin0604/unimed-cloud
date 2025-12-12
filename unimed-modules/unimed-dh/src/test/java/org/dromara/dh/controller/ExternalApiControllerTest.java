package org.dromara.dh.controller;

import org.dromara.dh.domain.dto.DigitalHumanListRequest;
import org.dromara.dh.domain.dto.DigitalHumanListResponse;
import org.dromara.dh.service.IDigitalHumanApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 外部API控制器测试
 *
 * @author unimed
 * @since 2.5.1
 */
@WebFluxTest(ExternalApiController.class)
class ExternalApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IDigitalHumanApiService digitalHumanApiService;

    @Test
    @DisplayName("查询数字人列表 - 成功场景")
    void getDigitalHumanList_Success() {
        // Given
        var mockResponse = new DigitalHumanListResponse();
        mockResponse.setCode(200);
        mockResponse.setMsg("查询成功");
        mockResponse.setTotal(1L);
        mockResponse.setRows(Collections.emptyList());

        when(digitalHumanApiService.getDigitalHumanList(any(DigitalHumanListRequest.class)))
            .thenReturn(Mono.just(mockResponse));

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/dh/external/digital-humans/list")
                .queryParam("pageNum", 1)
                .queryParam("pageSize", 10)
                .build())
            .header("X-API-Key", "test-key")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.code").isEqualTo(200)
            .jsonPath("$.data.msg").isEqualTo("查询成功")
            .jsonPath("$.data.total").isEqualTo(1);
    }

    @Test
    @DisplayName("查询数字人列表 - 带筛选条件")
    void getDigitalHumanList_WithFilters() {
        // Given
        var mockResponse = new DigitalHumanListResponse();
        mockResponse.setCode(200);
        mockResponse.setMsg("查询成功");
        mockResponse.setTotal(0L);
        mockResponse.setRows(Collections.emptyList());

        when(digitalHumanApiService.getDigitalHumanList(any(DigitalHumanListRequest.class)))
            .thenReturn(Mono.just(mockResponse));

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/dh/external/digital-humans/list")
                .queryParam("pageNum", 1)
                .queryParam("pageSize", 10)
                .queryParam("keyword", "护士")
                .queryParam("sex", "female")
                .queryParam("groupCategory", "医护")
                .build())
            .header("X-API-Key", "test-key")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data.code").isEqualTo(200);
    }
}