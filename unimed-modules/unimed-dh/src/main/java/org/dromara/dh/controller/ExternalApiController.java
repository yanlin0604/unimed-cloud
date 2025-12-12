package org.dromara.dh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.dh.domain.dto.*;
import org.dromara.dh.filter.ApiKeyAuthFilter;
import org.dromara.dh.service.IDigitalHumanApiService;
import org.dromara.dh.service.IWebRtcService;
import org.dromara.dh.service.IChatService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 外部 API 控制器
 *
 * <p>提供给外部系统调用的数字人接口，需要 API Key 鉴权</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Tag(name = "数字人服务 - 外部接口", description = "提供给外部系统调用的数字人接口，需要 API Key 鉴权")
@SecurityRequirement(name = "X-API-Key")
@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/dh/external")
@RequiredArgsConstructor
public class ExternalApiController extends BaseController {

    private final IDigitalHumanApiService digitalHumanApiService;
    private final IWebRtcService webRtcService;
    private final IChatService chatService;


    /**
     * 保存数字人配置
     *
     * <p>使用 Mono 的原因：</p>
     * <ul>
     *   <li>调用数字人服务，网络延迟不可预测</li>
     *   <li>非阻塞处理，提高并发性能</li>
     *   <li>线程不会在等待响应时被浪费</li>
     *   <li>支持响应式错误处理和重试</li>
     * </ul>
     */
    @Operation(summary = "保存数字人配置", description = "调用数字人服务保存配置信息")
    @Log(title = "数字人配置", businessType = BusinessType.UPDATE)
    @PostMapping("/digital-humans/config")
    public Mono<R<DhConfigResponse>> saveDigitalHumanConfig(
        HttpServletRequest request,
        @Valid @RequestBody DhConfigRequest configRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 保存数字人配置, 调用方: {}, 数字人ID: {}, 声音类型: {}",
            apiKeyName,
            configRequest.getConfigs().getAvatarId(),
            configRequest.getConfigs().getRefFile());

        return digitalHumanApiService.saveDigitalHumanConfig(configRequest)
            .map(response -> {
                log.info("数字人配置保存成功 - 调用方: {}, 更新数量: {}",
                    apiKeyName, response.getUpdatedCount());
                return R.ok(response);
            })
            .onErrorResume(throwable -> {
                log.error("数字人配置保存失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);
                return Mono.just(R.fail("保存数字人配置失败: " + throwable.getMessage()));
            });
    }

    /**
     * 建立数字人 WebRTC 连接
     */
    @Operation(summary = "建立数字人 WebRTC 连接", description = "处理客户端的 WebRTC SDP offer，建立音视频连接")
    @Log(title = "数字人WebRTC连接", businessType = BusinessType.INSERT)
    @PostMapping("/digital-humans/webrtc/offer")
    public Mono<R<WebRtcOfferResponse>> establishWebRtcConnection(
        HttpServletRequest request,
        @Valid @RequestBody WebRtcOfferRequest offerRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 建立 WebRTC 连接, 调用方: {}, 会话ID: {}, SDP类型: {}",
            apiKeyName,
            offerRequest.getSessionid(),
            offerRequest.getType());

        return webRtcService.establishConnection(offerRequest)
            .map(response -> {
                if (response.getCode() != null && response.getCode() != 0) {
                    log.warn("WebRTC 连接建立返回错误 - 调用方: {}, 错误码: {}, 消息: {}",
                        apiKeyName, response.getCode(), response.getMsg());
                    return R.<WebRtcOfferResponse>fail(response.getMsg());
                } else {
                    log.info("WebRTC 连接建立成功 - 调用方: {}, 会话ID: {}",
                        apiKeyName, response.getSessionid());
                    return R.ok(response);
                }
            })
            .onErrorResume(throwable -> {
                log.error("WebRTC 连接建立失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);

                // 处理冲突错误
                if (throwable.getMessage().contains("冲突")) {
                    return Mono.just(R.<WebRtcOfferResponse>fail(409, throwable.getMessage()));
                }

                return Mono.just(R.<WebRtcOfferResponse>fail("WebRTC 连接建立失败: " + throwable.getMessage()));
            });
    }

    /**
     * 获取数字人 WebRTC 连接状态
     */
    @Operation(summary = "获取数字人 WebRTC 连接状态", description = "获取所有 WebRTC 连接的状态统计信息")
    @Log(title = "数字人WebRTC状态", businessType = BusinessType.OTHER)
    @GetMapping("/digital-humans/webrtc/status")
    public Mono<R<WebRtcStatusResponse>> getWebRtcConnectionStatus(
        HttpServletRequest request
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 获取 WebRTC 连接状态, 调用方: {}", apiKeyName);

        return webRtcService.getConnectionStatus()
            .map(response -> {
                log.info("获取 WebRTC 连接状态成功 - 调用方: {}, 活跃连接: {}, 总会话: {}",
                    apiKeyName, response.getActiveConnections(), response.getTotalSessions());
                return R.ok(response);
            })
            .onErrorResume(throwable -> {
                log.error("获取 WebRTC 连接状态失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);
                return Mono.just(R.<WebRtcStatusResponse>fail("获取连接状态失败: " + throwable.getMessage()));
            });
    }

    /**
     * 发送文本消息给数字人
     */
    @Operation(summary = "发送文本消息给数字人", description = "支持直接播报和AI对话两种模式")
    @Log(title = "数字人对话", businessType = BusinessType.INSERT)
    @PostMapping("/digital-humans/chat")
    public Mono<R<ChatResponse>> sendTextMessage(
        HttpServletRequest request,
        @Valid @RequestBody ChatRequest chatRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 发送文本消息, 调用方: {}, 会话ID: {}, 类型: {}, 内容长度: {}, 是否打断: {}",
            apiKeyName,
            chatRequest.getSessionid(),
            chatRequest.getType(),
            chatRequest.getText().length(),
            chatRequest.getInterrupt());

        return chatService.sendTextMessage(chatRequest)
            .map(response -> {
                if (response.getCode() != null && response.getCode() != 0) {
                    log.warn("文本消息发送返回错误 - 调用方: {}, 错误码: {}, 消息: {}",
                        apiKeyName, response.getCode(), response.getMsg());
                    return R.<ChatResponse>fail(response.getMsg());
                } else {
                    log.info("文本消息发送成功 - 调用方: {}, 会话ID: {}",
                        apiKeyName, chatRequest.getSessionid());
                    return R.ok(response);
                }
            })
            .onErrorResume(throwable -> {
                log.error("文本消息发送失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);
                return Mono.just(R.<ChatResponse>fail("发送文本消息失败: " + throwable.getMessage()));
            });
    }

    /**
     * 打断数字人当前说话
     */
    @Operation(summary = "打断数字人当前说话", description = "立即停止数字人当前的说话，清空待播放的消息队列")
    @Log(title = "数字人打断", businessType = BusinessType.UPDATE)
    @PostMapping("/digital-humans/interrupt")
    public Mono<R<ChatResponse>> interruptTalk(
        HttpServletRequest request,
        @Valid @RequestBody InterruptRequest interruptRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 打断数字人说话, 调用方: {}, 会话ID: {}",
            apiKeyName, interruptRequest.getSessionid());

        return chatService.interruptTalk(interruptRequest)
            .map(response -> {
                if (response.getCode() != null && response.getCode() != 0) {
                    log.warn("打断操作返回错误 - 调用方: {}, 错误码: {}, 消息: {}",
                        apiKeyName, response.getCode(), response.getMsg());
                    return R.<ChatResponse>fail(response.getMsg());
                } else {
                    log.info("打断操作成功 - 调用方: {}, 会话ID: {}",
                        apiKeyName, interruptRequest.getSessionid());
                    return R.ok(response);
                }
            })
            .onErrorResume(throwable -> {
                log.error("打断操作失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);
                return Mono.just(R.<ChatResponse>fail("打断操作失败: " + throwable.getMessage()));
            });
    }

    /**
     * 查询数字人是否正在说话
     */
    @Operation(summary = "查询数字人是否正在说话", description = "获取数字人当前的说话状态和相关信息")
    @Log(title = "数字人状态查询", businessType = BusinessType.OTHER)
    @PostMapping("/digital-humans/speaking-status")
    public Mono<R<SpeakingStatusResponse>> getSpeakingStatus(
        HttpServletRequest request,
        @Valid @RequestBody SpeakingStatusRequest statusRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 查询说话状态, 调用方: {}, 会话ID: {}",
            apiKeyName, statusRequest.getSessionid());

        return chatService.getSpeakingStatus(statusRequest)
            .map(response -> {
                if (response.getCode() != null && response.getCode() == 0 && response.getData() != null) {
                    log.info("查询说话状态成功 - 调用方: {}, 会话ID: {}, 是否说话: {}",
                        apiKeyName, statusRequest.getSessionid(), response.getData().getIsSpeaking());
                    return R.ok(response);
                } else {
                    log.warn("查询说话状态返回错误 - 调用方: {}, 错误码: {}",
                        apiKeyName, response.getCode());
                    return R.<SpeakingStatusResponse>fail("查询说话状态失败");
                }
            })
            .onErrorResume(throwable -> {
                log.error("查询说话状态失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);
                return Mono.just(R.<SpeakingStatusResponse>fail("查询说话状态失败: " + throwable.getMessage()));
            });
    }

    /**
     * 查询数字人列表
     */
    @Operation(summary = "查询数字人列表", description = "分页查询数字人列表，支持按关键词、性别、分组类别筛选")
    @Log(title = "数字人列表查询", businessType = BusinessType.OTHER)
    @GetMapping("/digital-humans/list")
    public Mono<R<DigitalHumanListResponse>> getDigitalHumanList(
        HttpServletRequest request,
        @Valid DigitalHumanListRequest listRequest
    ) {
        var apiKeyName = getApiKeyName(request);

        return digitalHumanApiService.getDigitalHumanList(listRequest)
            .map(response -> {
                if (response.getCode() != null && response.getCode() == 200) {
                    log.info("数字人列表查询成功 - 调用方: {}, 总数: {}, 返回数量: {}",
                        apiKeyName, response.getTotal(),
                        response.getRows() != null ? response.getRows().size() : 0);
                    return R.ok(response);
                } else {
                    log.warn("数字人列表查询返回错误 - 调用方: {}, 错误码: {}, 消息: {}",
                        apiKeyName, response.getCode(), response.getMsg());
                    return R.<DigitalHumanListResponse>fail(response.getMsg());
                }
            })
            .onErrorResume(throwable -> {
                log.error("数字人列表查询失败 - 调用方: {}, 错误: {}",
                    apiKeyName, throwable.getMessage(), throwable);
                return Mono.just(R.<DigitalHumanListResponse>fail("查询数字人列表失败: " + throwable.getMessage()));
            });
    }

    /**
     * 删除数字人
     */
    @Operation(summary = "删除数字人", description = "删除指定的数字人，会同时调用数字人服务和训练服务的删除接口")
    @Log(title = "数字人删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/digital-humans/{digitalHumanId}")
    public Mono<R<DigitalHumanDeleteResponse>> deleteDigitalHuman(
        HttpServletRequest request,
        @PathVariable("digitalHumanId") String digitalHumanId
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 删除数字人, 调用方: {}, 数字人ID: {}", apiKeyName, digitalHumanId);

        return digitalHumanApiService.deleteDigitalHuman(digitalHumanId)
            .map(response -> {
                if (response.getSuccess() != null && response.getSuccess()) {
                    log.info("数字人删除成功 - 调用方: {}, 数字人ID: {}", apiKeyName, digitalHumanId);
                    return R.ok(response);
                } else {
                    log.warn("数字人删除失败 - 调用方: {}, 数字人ID: {}, 错误: {}",
                        apiKeyName, digitalHumanId, response.getMessage());
                    return R.<DigitalHumanDeleteResponse>fail(response.getMessage());
                }
            })
            .onErrorResume(throwable -> {
                log.error("数字人删除失败 - 调用方: {}, 数字人ID: {}, 错误: {}",
                    apiKeyName, digitalHumanId, throwable.getMessage(), throwable);
                return Mono.just(R.<DigitalHumanDeleteResponse>fail("删除数字人失败: " + throwable.getMessage()));
            });
    }

    /**
     * 上传视频并开始训练
     */
    @Operation(summary = "上传视频并开始训练", description = "上传视频到数字人服务并启动训练任务，两个操作会并行执行")
    @Log(title = "视频上传训练", businessType = BusinessType.INSERT)
    @PostMapping("/digital-humans/upload-and-train")
    public Mono<R<VideoUploadTrainResponse>> uploadVideoAndTrain(
        HttpServletRequest request,
        @Valid @RequestBody VideoUploadTrainRequest uploadTrainRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 上传视频并训练, 调用方: {}, 形象标题: {}, 性别: {}, 训练类型: {}, 是否强制重训: {}",
            apiKeyName,
            uploadTrainRequest.getFigureTitle(),
            uploadTrainRequest.getSex(),
            uploadTrainRequest.getType(),
            uploadTrainRequest.getForceRetrain());

        return digitalHumanApiService.uploadVideoAndTrain(uploadTrainRequest)
            .map(response -> {
                if (response.getSuccess() != null && response.getSuccess()) {
                    log.info("视频上传和训练启动成功 - 调用方: {}, 形象标题: {}, 数字人ID: {}, 任务ID: {}",
                        apiKeyName, uploadTrainRequest.getFigureTitle(), response.getDigitalId(), response.getTaskId());
                    return R.ok(response);
                } else {
                    log.warn("视频上传和训练失败 - 调用方: {}, 形象标题: {}, 错误: {}",
                        apiKeyName, uploadTrainRequest.getFigureTitle(), response.getMessage());
                    return R.<VideoUploadTrainResponse>fail(response.getMessage());
                }
            })
            .onErrorResume(throwable -> {
                log.error("视频上传和训练失败 - 调用方: {}, 形象标题: {}, 错误: {}",
                    apiKeyName, uploadTrainRequest.getFigureTitle(), throwable.getMessage(), throwable);
                return Mono.just(R.<VideoUploadTrainResponse>fail("视频上传和训练失败: " + throwable.getMessage()));
            });
    }

    /**
     * 查询训练进度
     */
    @Operation(summary = "查询训练进度", description = "根据任务ID查询数字人训练的当前进度和状态")
    @Log(title = "训练进度查询", businessType = BusinessType.OTHER)
    @GetMapping("/digital-humans/training/progress/{taskId}")
    public Mono<R<TrainingProgressResponse>> getTrainingProgress(
        HttpServletRequest request,
        @PathVariable("taskId") String taskId
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 查询训练进度, 调用方: {}, 任务ID: {}", apiKeyName, taskId);

        return digitalHumanApiService.getTrainingProgress(taskId)
            .map(response -> {
                if (response.getSuccess() != null && response.getSuccess()) {
                    log.info("训练进度查询成功 - 调用方: {}, 任务ID: {}, 状态: {}, 进度: {}%",
                        apiKeyName, taskId, response.getStatus(), response.getProgress());
                    return R.ok(response);
                } else {
                    log.warn("训练进度查询失败 - 调用方: {}, 任务ID: {}, 错误: {}",
                        apiKeyName, taskId, response.getMessage());
                    return R.<TrainingProgressResponse>fail("查询训练进度失败");
                }
            })
            .onErrorResume(throwable -> {
                log.error("训练进度查询失败 - 调用方: {}, 任务ID: {}, 错误: {}",
                    apiKeyName, taskId, throwable.getMessage(), throwable);
                return Mono.just(R.<TrainingProgressResponse>fail("查询训练进度失败: " + throwable.getMessage()));
            });
    }

    /**
     * 修改数字人状态
     */
    @Operation(summary = "修改数字人状态", description = "更新数字人的视频合成状态和相关信息")
    @Log(title = "数字人状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/digital-humans/status")
    public Mono<R<StatusUpdateResponse>> updateDigitalHumanStatus(
        HttpServletRequest request,
        @Valid @RequestBody StatusUpdateRequest statusRequest
    ) {
        var apiKeyName = getApiKeyName(request);
        log.info("外部接口调用 - 修改数字人状态, 调用方: {}, 数字人ID: {}, 视频合成状态: {}, 训练人物ID: {}",
            apiKeyName,
            statusRequest.getId(),
            statusRequest.getVideoComposeState(),
            statusRequest.getTrainHumanId());

        return digitalHumanApiService.updateDigitalHumanStatus(statusRequest)
            .map(response -> {
                if (response.getCode() != null && response.getCode() == 200) {
                    log.info("数字人状态修改成功 - 调用方: {}, 数字人ID: {}, 消息: {}",
                        apiKeyName, statusRequest.getId(), response.getMsg());
                    return R.ok(response);
                } else {
                    log.warn("数字人状态修改失败 - 调用方: {}, 数字人ID: {}, 错误码: {}, 消息: {}",
                        apiKeyName, statusRequest.getId(), response.getCode(), response.getMsg());
                    return R.<StatusUpdateResponse>fail(response.getMsg());
                }
            })
            .onErrorResume(throwable -> {
                log.error("数字人状态修改失败 - 调用方: {}, 数字人ID: {}, 错误: {}",
                    apiKeyName, statusRequest.getId(), throwable.getMessage(), throwable);
                return Mono.just(R.<StatusUpdateResponse>fail("修改数字人状态失败: " + throwable.getMessage()));
            });
    }



    /**
     * 获取 API Key 名称
     */
    private String getApiKeyName(HttpServletRequest request) {
        var name = request.getAttribute(ApiKeyAuthFilter.API_KEY_NAME_ATTRIBUTE);
        return name != null ? name.toString() : "unknown";
    }
}
