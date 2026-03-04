package org.dromara.dh.service;

import org.dromara.dh.service.impl.ChatServiceImpl;

import org.dromara.dh.domain.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 聊天服务测试类
 *
 * @author unimed
 * @since 2.5.1
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    @DisplayName("ChatRequest 对象创建测试")
    void chatRequest_Creation() {
        // Given & When
        var request = new ChatRequest();
        request.setText("你好，数字人");
        request.setType(ChatRequest.ChatType.echo);
        request.setInterrupt(false);
        request.setSessionid("123456");

        // Then
        assertThat(request.getText()).isEqualTo("你好，数字人");
        assertThat(request.getType()).isEqualTo(ChatRequest.ChatType.echo);
        assertThat(request.getInterrupt()).isFalse();
        assertThat(request.getSessionid()).isEqualTo("123456");
    }

    @Test
    @DisplayName("ChatResponse 对象创建测试")
    void chatResponse_Creation() {
        // Given & When
        var response = new ChatResponse();
        response.setCode(0);
        response.setMsg("ok");

        // Then
        assertThat(response.getCode()).isEqualTo(0);
        assertThat(response.getMsg()).isEqualTo("ok");
    }

    @Test
    @DisplayName("InterruptRequest 对象创建测试")
    void interruptRequest_Creation() {
        // Given & When
        var request = new InterruptRequest();
        request.setSessionid("123456");

        // Then
        assertThat(request.getSessionid()).isEqualTo("123456");
    }

    @Test
    @DisplayName("SpeakingStatusRequest 对象创建测试")
    void speakingStatusRequest_Creation() {
        // Given & When
        var request = new SpeakingStatusRequest();
        request.setSessionid("123456");

        // Then
        assertThat(request.getSessionid()).isEqualTo("123456");
    }

    @Test
    @DisplayName("SpeakingStatusResponse 对象创建测试")
    void speakingStatusResponse_Creation() {
        // Given & When
        var speakingData = new SpeakingStatusResponse.SpeakingData();
        speakingData.setIsSpeaking(true);
        speakingData.setCurrentAudiotype("default");

        var response = new SpeakingStatusResponse();
        response.setCode(0);
        response.setData(speakingData);

        // Then
        assertThat(response.getCode()).isEqualTo(0);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getIsSpeaking()).isTrue();
        assertThat(response.getData().getCurrentAudiotype()).isEqualTo("default");
    }

    @Test
    @DisplayName("ChatType 枚举测试")
    void chatType_Enum() {
        // Given & When & Then
        assertThat(ChatRequest.ChatType.echo).isNotNull();
        assertThat(ChatRequest.ChatType.chat).isNotNull();
        assertThat(ChatRequest.ChatType.values()).hasSize(2);
    }
}