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
    private final WebClient digitalHumanListWebClient;

    /**
     * 构造函数注入 WebClient 实例
     */
    public DigitalHumanApiServiceImpl(
        @Qualifier("digitalHumanWebClient") WebClient webClient,
        @Qualifier("digitalHumanListWebClient") WebClient digitalHumanListWebClient
    ) {
        this.webClient = webClient;
        this.digitalHumanListWebClient = digitalHumanListWebClient;
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
     * 查询数字人列表
     *
     * @param request 查询请求
     * @return 数字人列表响应
     */
    @Override
    public Mono<DigitalHumanListResponse> getDigitalHumanList(DigitalHumanListRequest request) {

        return digitalHumanListWebClient.get()
            .uri(uriBuilder -> {
                var builder = uriBuilder.path("/ai/digital/list")
                    .queryParam("pageNum", request.getPageNum())
                    .queryParam("pageSize", request.getPageSize());

                // 添加可选查询参数
//                if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
//                    builder.queryParam("keyword", request.getKeyword());
//                }
//                if (request.getSex() != null && !request.getSex().isBlank()) {
//                    builder.queryParam("sex", request.getSex());
//                }
//                if (request.getGroupCategory() != null && !request.getGroupCategory().isBlank()) {
//                    builder.queryParam("groupCategory", request.getGroupCategory());
//                }

                return builder.build();
            })
            .retrieve()
            .bodyToMono(DigitalHumanListResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("查询数字人列表失败，正在重试 - 重试次数: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.info("数字人列表查询成功 - 总数: {}, 返回数量: {}",
                    response.getTotal(),
                    response.getRows() != null ? response.getRows().size() : 0))
            .doOnError(error ->
                log.error("查询数字人列表失败 - 页码: {}, 错误: {}",
                    request.getPageNum(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("数字人列表服务返回错误状态码: {} - 响应体: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("调用数字人列表服务失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("查询数字人列表时发生未知错误: " + ex.getMessage(), ex);
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

        // 并行调用两个删除接口
        var digitalServiceDelete = deleteFromDigitalService(digitalHumanId);
        var trainingServiceDelete = deleteFromTrainingService(digitalHumanId);

        return Mono.zip(digitalServiceDelete, trainingServiceDelete)
            .map(tuple -> {
                var digitalResult = tuple.getT1();
                var trainingResult = tuple.getT2();

                var response = new DigitalHumanDeleteResponse();
                response.setDigitalServiceResult(digitalResult);
                response.setTrainingServiceResult(trainingResult);

                // 判断整体删除是否成功
                boolean digitalSuccess = digitalResult.getCode() != null && digitalResult.getCode() == 200;
                boolean trainingSuccess = trainingResult.getSuccess() != null && trainingResult.getSuccess();

                if (digitalSuccess && trainingSuccess) {
                    response.setSuccess(true);
                    response.setMessage("数字人删除成功");
                    log.info("数字人删除成功 - 数字人ID: {}", digitalHumanId);
                } else {
                    response.setSuccess(false);
                    var errorMsg = new StringBuilder("数字人删除部分失败: ");
                    if (!digitalSuccess) {
                        errorMsg.append("数字人服务删除失败(").append(digitalResult.getMsg()).append(") ");
                    }
                    if (!trainingSuccess) {
                        errorMsg.append("训练服务删除失败(").append(trainingResult.getMessage()).append(")");
                    }
                    response.setMessage(errorMsg.toString());
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
     * 从数字人服务删除
     */
    private Mono<DigitalHumanDeleteResponse.DigitalServiceDeleteResult> deleteFromDigitalService(String digitalHumanId) {
        log.debug("调用数字人服务删除接口 - 数字人ID: {}", digitalHumanId);

        return digitalHumanListWebClient.delete()
            .uri("/ai/digital/{id}", digitalHumanId)
            .retrieve()
            .bodyToMono(DigitalHumanDeleteResponse.DigitalServiceDeleteResult.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("数字人服务删除失败，正在重试 - 重试次数: {}, 数字人ID: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, digitalHumanId, retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.debug("数字人服务删除调用完成 - 数字人ID: {}, 响应码: {}", digitalHumanId, response.getCode()))
            .onErrorResume(throwable -> {
                log.error("数字人服务删除失败 - 数字人ID: {}, 错误: {}", digitalHumanId, throwable.getMessage());
                var errorResult = new DigitalHumanDeleteResponse.DigitalServiceDeleteResult();
                errorResult.setCode(500);
                errorResult.setMsg("数字人服务删除失败: " + throwable.getMessage());
                return Mono.just(errorResult);
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
     * 上传视频并开始训练
     *
     * @param request 上传训练请求
     * @return 上传训练响应
     */
    @Override
    public Mono<VideoUploadTrainResponse> uploadVideoAndTrain(VideoUploadTrainRequest request) {
        log.info("开始上传视频并训练 - 形象标题: {}, 性别: {}, 训练类型: {}",
            request.getFigureTitle(), request.getSex(), request.getType());

        // 并行调用上传和训练接口
        var uploadMono = uploadVideoToDigitalService(request);
        
        return uploadMono.flatMap(uploadResult -> {
            if (uploadResult.getCode() != null && uploadResult.getCode() == 200) {
                // 上传成功，开始训练
                var digitalId = extractDigitalId(uploadResult.getData());
                return trainVideoModel(request, digitalId)
                    .map(trainingResult -> {
                        var response = new VideoUploadTrainResponse();
                        response.setUploadResult(uploadResult);
                        response.setTrainingResult(trainingResult);
                        response.setDigitalId(digitalId);
                        response.setTaskId(trainingResult.getTaskId());
                        
                        boolean uploadSuccess = uploadResult.getCode() == 200;
                        boolean trainingSuccess = trainingResult.getSuccess() != null && trainingResult.getSuccess();
                        
                        if (uploadSuccess && trainingSuccess) {
                            response.setSuccess(true);
                            response.setMessage("视频上传和训练启动成功");
                            log.info("视频上传和训练启动成功 - 形象标题: {}, 数字人ID: {}, 任务ID: {}",
                                request.getFigureTitle(), digitalId, trainingResult.getTaskId());
                        } else {
                            response.setSuccess(false);
                            var errorMsg = new StringBuilder("操作部分失败: ");
                            if (!uploadSuccess) {
                                errorMsg.append("上传失败(").append(uploadResult.getMsg()).append(") ");
                            }
                            if (!trainingSuccess) {
                                errorMsg.append("训练启动失败(").append(trainingResult.getMessage()).append(")");
                            }
                            response.setMessage(errorMsg.toString());
                            log.warn("视频上传和训练部分失败 - 形象标题: {}, 错误: {}", 
                                request.getFigureTitle(), errorMsg);
                        }
                        
                        return response;
                    });
            } else {
                // 上传失败，不进行训练
                var response = new VideoUploadTrainResponse();
                response.setUploadResult(uploadResult);
                response.setSuccess(false);
                response.setMessage("视频上传失败: " + uploadResult.getMsg());
                log.error("视频上传失败 - 形象标题: {}, 错误: {}", 
                    request.getFigureTitle(), uploadResult.getMsg());
                return Mono.just(response);
            }
        })
        .doOnError(error ->
            log.error("上传视频并训练失败 - 形象标题: {}, 错误: {}", 
                request.getFigureTitle(), error.getMessage(), error))
        .onErrorMap(Exception.class, ex -> {
            if (!(ex instanceof RuntimeException)) {
                return new RuntimeException("上传视频并训练时发生未知错误: " + ex.getMessage(), ex);
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
     * 修改数字人状态
     *
     * @param request 状态修改请求
     * @return 状态修改响应
     */
    @Override
    public Mono<StatusUpdateResponse> updateDigitalHumanStatus(StatusUpdateRequest request) {
        log.info("开始修改数字人状态 - 数字人ID: {}, 视频合成状态: {}, 训练人物ID: {}",
            request.getId(), request.getVideoComposeState(), request.getTrainHumanId());

        return digitalHumanListWebClient.put()
            .uri("/ai/digital")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(StatusUpdateResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("修改数字人状态失败，正在重试 - 重试次数: {}, 数字人ID: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, request.getId(), retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.info("数字人状态修改成功 - 数字人ID: {}, 响应码: {}, 消息: {}",
                    request.getId(), response.getCode(), response.getMsg()))
            .doOnError(error ->
                log.error("修改数字人状态失败 - 数字人ID: {}, 错误: {}", 
                    request.getId(), error.getMessage(), error))
            .onErrorMap(WebClientResponseException.class, ex -> {
                log.error("数字人状态服务返回错误状态码: {} - 响应体: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
                return new RuntimeException("调用数字人状态服务失败: " + ex.getMessage(), ex);
            })
            .onErrorMap(Exception.class, ex -> {
                if (!(ex instanceof RuntimeException)) {
                    return new RuntimeException("修改数字人状态时发生未知错误: " + ex.getMessage(), ex);
                }
                return ex;
            });
    }

    /**
     * 上传视频到数字人服务
     */
    private Mono<VideoUploadTrainResponse.UploadServiceResult> uploadVideoToDigitalService(VideoUploadTrainRequest request) {
        log.debug("调用数字人服务上传视频 - 形象标题: {}", request.getFigureTitle());

        // 构建上传请求
        var uploadRequest = new UploadVideoRequest();
        uploadRequest.setSilentVideoUrl(request.getSilentVideoUrl());
        uploadRequest.setActionVideoUrl(request.getActionVideoUrl());
        uploadRequest.setFigureTitle(request.getFigureTitle());
        uploadRequest.setSex(request.getSex());
        uploadRequest.setFigureIntroduction(request.getFigureIntroduction());
        uploadRequest.setChangeBackground(request.getChangeBackground());
        uploadRequest.setReplaceBg(request.getReplaceBg());
        uploadRequest.setVoiceFile(request.getVoiceFile());
        uploadRequest.setAgree(request.getAgree());

        return digitalHumanListWebClient.post()
            .uri("/ai/digital")
            .bodyValue(uploadRequest)
            .retrieve()
            .bodyToMono(VideoUploadTrainResponse.UploadServiceResult.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                .doBeforeRetry(retrySignal ->
                    log.warn("上传视频失败，正在重试 - 重试次数: {}, 形象标题: {}, 错误: {}",
                        retrySignal.totalRetries() + 1, request.getFigureTitle(), retrySignal.failure().getMessage())))
            .doOnSuccess(response ->
                log.debug("视频上传调用完成 - 形象标题: {}, 响应码: {}", 
                    request.getFigureTitle(), response.getCode()))
            .onErrorResume(throwable -> {
                log.error("视频上传失败 - 形象标题: {}, 错误: {}", 
                    request.getFigureTitle(), throwable.getMessage());
                var errorResult = new VideoUploadTrainResponse.UploadServiceResult();
                errorResult.setCode(500);
                errorResult.setMsg("视频上传失败: " + throwable.getMessage());
                return Mono.just(errorResult);
            });
    }

    /**
     * 训练视频模型
     */
    private Mono<VideoUploadTrainResponse.TrainingServiceResult> trainVideoModel(VideoUploadTrainRequest request, String digitalId) {
        log.debug("调用训练服务训练模型 - 形象标题: {}, 数字人ID: {}", request.getFigureTitle(), digitalId);

        // 构建训练请求
        var trainRequest = new TrainVideoRequest();
        trainRequest.setVideoName(request.getFigureTitle());
        trainRequest.setTaskId(digitalId);
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
     * 从上传响应中提取数字人ID
     */
    private String extractDigitalId(String data) {
        if (data != null && data.startsWith("{\"digitalId\":\"") && data.endsWith("\"}")) {
            return data.substring(14, data.length() - 2);
        }
        return data;
    }

    /**
     * 上传视频请求（内部使用）
     */
    private static class UploadVideoRequest {
        private String silentVideoUrl;
        private String actionVideoUrl;
        private String figureTitle;
        private String sex;
        private String figureIntroduction;
        private Boolean changeBackground;
        private String replaceBg;
        private String voiceFile;
        private Boolean agree;

        // Getters and Setters
        public String getSilentVideoUrl() { return silentVideoUrl; }
        public void setSilentVideoUrl(String silentVideoUrl) { this.silentVideoUrl = silentVideoUrl; }
        public String getActionVideoUrl() { return actionVideoUrl; }
        public void setActionVideoUrl(String actionVideoUrl) { this.actionVideoUrl = actionVideoUrl; }
        public String getFigureTitle() { return figureTitle; }
        public void setFigureTitle(String figureTitle) { this.figureTitle = figureTitle; }
        public String getSex() { return sex; }
        public void setSex(String sex) { this.sex = sex; }
        public String getFigureIntroduction() { return figureIntroduction; }
        public void setFigureIntroduction(String figureIntroduction) { this.figureIntroduction = figureIntroduction; }
        public Boolean getChangeBackground() { return changeBackground; }
        public void setChangeBackground(Boolean changeBackground) { this.changeBackground = changeBackground; }
        public String getReplaceBg() { return replaceBg; }
        public void setReplaceBg(String replaceBg) { this.replaceBg = replaceBg; }
        public String getVoiceFile() { return voiceFile; }
        public void setVoiceFile(String voiceFile) { this.voiceFile = voiceFile; }
        public Boolean getAgree() { return agree; }
        public void setAgree(Boolean agree) { this.agree = agree; }
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
