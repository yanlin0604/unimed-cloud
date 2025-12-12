package org.dromara.dh.service.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DigitalHumanApiServiceImpl implements IDigitalHumanApiService {

    @Qualifier("digitalHumanWebClient")
    private final WebClient webClient;

    @Qualifier("digitalHumanListWebClient")
    private final WebClient digitalHumanListWebClient;

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
}
