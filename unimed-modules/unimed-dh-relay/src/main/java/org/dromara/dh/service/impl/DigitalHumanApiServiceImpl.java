package org.dromara.dh.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.domain.dto.*;
import org.dromara.dh.service.IDigitalHumanApiService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * 数字人服务实现类
 *
 * <p>负责与数字人服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
public class DigitalHumanApiServiceImpl implements IDigitalHumanApiService {

    private final WebClient webClient;
//    private final WebClient digitalHumanListWebClient;

    /**
     * 构造函数注入 WebClient 实例
     */
    public DigitalHumanApiServiceImpl(
        @Qualifier("digitalHumanWebClient") WebClient webClient,
        @Qualifier("digitalHumanListWebClient") WebClient digitalHumanListWebClient
    ) {
        this.webClient = webClient;
//        this.digitalHumanListWebClient = digitalHumanListWebClient;
    }

    /**
     * 保存数字人配置
     *
     * @param request 配置请求（Java 驼峰格式）
     * @return 配置响应
     */
    @Override
    public Mono<DhConfigResponse> saveDigitalHumanConfig(DhConfigRequest request) {
        log.info("开始调用数字人服务保存配置 - 数字人ID: {}, 声音类型: {}",
            request.getConfigs().getAvatarId(), request.getConfigs().getRefFile());

        // 转换为数字人服务需要的格式
        var serviceRequest = DhServiceRequest.from(request);

        log.debug("转换后的数字人服务请求格式: {}", serviceRequest);

        return webClient.post()
            .uri("/update_config")
            .bodyValue(serviceRequest)  // 使用转换后的请求格式
            .retrieve()
            .bodyToMono(DhConfigResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("调用数字人服务失败，正在重试 - 重试次数: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.info("数字人服务调用成功 - 响应: {}", response))
            .doOnError(error ->
                log.error("调用数字人服务失败 - 数字人ID: {}, 错误: {}",
                    request.getConfigs().getAvatarId(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("数字人服务返回错误状态码: {} - 响应体: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("调用数字人配置服务失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("保存数字人配置时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }


    /**
     * 删除数字人
     *
     * @param digitalHumanId 数字人ID
     * @return 删除响应
     */
    @Override
    public Mono<DigitalHumanDeleteResponse> deleteDigitalHuman(String digitalHumanId) {
        log.info("开始删除数字人 - 数字人ID: {}", digitalHumanId);

        // 调用训练服务删除接口
        var trainingServiceDelete = deleteFromTrainingService(digitalHumanId);

        return trainingServiceDelete
            .map(trainingResult -> {
                var response = new DigitalHumanDeleteResponse();
                response.setTrainingServiceResult(trainingResult);

                // 判断整体删除是否成功
                boolean trainingSuccess = Boolean.TRUE.equals(trainingResult.getSuccess());

                if (trainingSuccess) {
                    response.setSuccess(true);
                    response.setMessage("数字人删除成功");
                    log.info("数字人删除成功 - 数字人ID: {}", digitalHumanId);
                } else {
                    response.setSuccess(false);
                    var errorMsg = "数字人删除部分失败: 训练服务删除失败(" + trainingResult.getMessage() + ")";
                    response.setMessage(errorMsg);
                    log.warn("数字人删除部分失败 - 数字人ID: {}, 错误: {}", digitalHumanId, errorMsg);
                }

                return response;
            })
            .doOnError(error ->
                log.error("删除数字人失败 - 数字人ID: {}, 错误: {}", digitalHumanId, error.getMessage(), error))
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("删除数字人时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }


    /**
     * 从训练服务删除
     */
    private Mono<DigitalHumanDeleteResponse.TrainingServiceDeleteResult> deleteFromTrainingService(String digitalHumanId) {
        log.debug("调用训练服务删除接口 - 数字人ID: {}", digitalHumanId);

        return webClient.delete()
            .uri("/training/delete/{id}", digitalHumanId)
            .retrieve()
            .bodyToMono(DigitalHumanDeleteResponse.TrainingServiceDeleteResult.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("训练服务删除失败，正在重试 - 重试次数: {}, 数字人ID: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, digitalHumanId, retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.debug("训练服务删除调用完成 - 数字人ID: {}, 任务ID: {}", digitalHumanId, response.getTaskId()))
            .onErrorResume(throwable -> {
                log.error("训练服务删除失败 - 数字人ID: {}, 错误: {}", digitalHumanId, throwable.getMessage());
                var errorResult = new DigitalHumanDeleteResponse.TrainingServiceDeleteResult();
                errorResult.setSuccess(false);
                errorResult.setMessage("训练服务删除失败: " + throwable.getMessage());
                return Mono.just(errorResult);
            });
    }

    /**
     * 启动训练任务
     *
     * @param request 训练请求
     * @return 训练响应
     */
    @Override
    public Mono<VideoUploadTrainResponse> uploadVideoAndTrain(VideoUploadTrainRequest request) {
        log.info("开始启动训练任务 - 形象标题: {}, 数字人ID: {}, 训练类型: {}",
            request.getFigureTitle(), request.getDigitalId(), request.getType());

        return trainVideoModel(request)
            .map(trainingResult -> {
                var response = new VideoUploadTrainResponse();
                response.setTrainingResult(trainingResult);
                response.setDigitalId(request.getDigitalId());
                response.setTaskId(trainingResult.getTaskId());

                boolean trainingSuccess = Boolean.TRUE.equals(trainingResult.getSuccess());
                if (trainingSuccess) {
                    response.setSuccess(true);
                    response.setMessage("训练启动成功");
                    log.info("训练启动成功 - 形象标题: {}, 数字人ID: {}, 任务ID: {}",
                        request.getFigureTitle(), request.getDigitalId(), trainingResult.getTaskId());
                } else {
                    response.setSuccess(false);
                    var errorMsg = "训练启动失败: " + trainingResult.getMessage();
                    response.setMessage(errorMsg);
                    log.warn("训练启动失败 - 形象标题: {}, 数字人ID: {}, 错误: {}",
                        request.getFigureTitle(), request.getDigitalId(), errorMsg);
                }

                return response;
            })
        .doOnError(error ->
            log.error("启动训练任务失败 - 形象标题: {}, 数字人ID: {}, 错误: {}",
                request.getFigureTitle(), request.getDigitalId(), error.getMessage(), error))
        .onErrorMap(Exception.class, ex -> {
            if (!(ex instanceof RuntimeException)) {
                return new RuntimeException("启动训练任务时发生未知错误: " + ex.getMessage(), ex);
            }
            return ex;
        });
    }

    /**
     * 查询训练进度
     *
     * @param taskId 任务ID
     * @return 训练进度响应
     */
    @Override
    public Mono<TrainingProgressResponse> getTrainingProgress(String taskId) {
        log.info("开始查询训练进度 - 任务ID: {}", taskId);

        return webClient.get()
            .uri("/training/progress/{taskId}", taskId)
            .retrieve()
            .bodyToMono(TrainingProgressResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("查询训练进度失败，正在重试 - 重试次数: {}, 任务ID: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, taskId, retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.info("训练进度查询成功 - 任务ID: {}, 状态: {}, 进度: {}%",
                    taskId, response.getStatus(), response.getProgress()))
            .doOnError(error ->
                log.error("查询训练进度失败 - 任务ID: {}, 错误: {}", taskId, error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("训练进度服务返回错误状态码: {} - 响应体: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("调用训练进度服务失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("查询训练进度时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }




    /**
     * 训练视频模型
     */
    private Mono<VideoUploadTrainResponse.TrainingServiceResult> trainVideoModel(VideoUploadTrainRequest request) {
        log.debug("调用训练服务训练模型 - 形象标题: {}, 数字人ID: {}", request.getFigureTitle(), request.getDigitalId());

        // 构建训练请求
        var trainRequest = new TrainVideoRequest();
        trainRequest.setVideoName(request.getFigureTitle());
        trainRequest.setTaskId(request.getDigitalId());
        trainRequest.setForceRetrain(request.getForceRetrain());
        trainRequest.setVideoUrl(request.getSilentVideoUrl());
        trainRequest.setType(request.getType());

        return webClient.post()
            .uri("/train_video")
            .bodyValue(trainRequest)
            .retrieve()
            .bodyToMono(VideoUploadTrainResponse.TrainingServiceResult.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("训练模型失败，正在重试 - 重试次数: {}, 形象标题: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, request.getFigureTitle(), retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.debug("模型训练调用完成 - 形象标题: {}, 任务ID: {}",
                    request.getFigureTitle(), response.getTaskId()))
            .onErrorResume(throwable -> {
                log.error("模型训练失败 - 形象标题: {}, 错误: {}",
                    request.getFigureTitle(), throwable.getMessage());
                var errorResult = new VideoUploadTrainResponse.TrainingServiceResult();
                errorResult.setSuccess(false);
                errorResult.setMessage("模型训练失败: " + throwable.getMessage());
                return Mono.just(errorResult);
            });
    }

    /**
     * 训练视频请求（内部使用）
     */
    private static class TrainVideoRequest {
        private String videoName;
        private String taskId;
        private Boolean forceRetrain;
        private String videoUrl;
        private String type;

        // Getters and Setters
        public String getVideoName() { return videoName; }
        public void setVideoName(String videoName) { this.videoName = videoName; }
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public Boolean getForceRetrain() { return forceRetrain; }
        public void setForceRetrain(Boolean forceRetrain) { this.forceRetrain = forceRetrain; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
