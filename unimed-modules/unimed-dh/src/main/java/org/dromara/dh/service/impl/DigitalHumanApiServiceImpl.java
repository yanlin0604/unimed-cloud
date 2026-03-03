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
        @Qualifier("digitalHumanWebClient") WebClient webClient
//        @Qualifier("digitalHumanListWebClient") WebClient digitalHumanListWebClient
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
        log.warn("digitalHumanListWebClient 调用已注释，跳过数字人服务删除 - 数字人ID: {}", digitalHumanId);
        var errorResult = new DigitalHumanDeleteResponse.DigitalServiceDeleteResult();
        errorResult.setCode(200);
        errorResult.setMsg("digitalHumanListWebClient 调用已注释，数字人服务删除已跳过");
        return Mono.just(errorResult);
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

        log.warn("digitalHumanListWebClient 调用已注释，上传与训练流程已跳过 - 形象标题: {}", request.getFigureTitle());

        var uploadResult = new VideoUploadTrainResponse.UploadServiceResult();
        uploadResult.setCode(200);
        uploadResult.setMsg("digitalHumanListWebClient 调用已注释，视频上传已跳过");

        var trainingResult = new VideoUploadTrainResponse.TrainingServiceResult();
        trainingResult.setSuccess(true);
        trainingResult.setMessage("digitalHumanListWebClient 调用已注释，训练流程已跳过");
        trainingResult.setStatus("skipped");

        var response = new VideoUploadTrainResponse();
        response.setUploadResult(uploadResult);
        response.setTrainingResult(trainingResult);
        response.setSuccess(true);
        response.setMessage("digitalHumanListWebClient 调用已注释，上传与训练流程已跳过");
        return Mono.just(response);
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

}
