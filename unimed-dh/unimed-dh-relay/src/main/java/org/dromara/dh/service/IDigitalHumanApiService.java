package org.dromara.dh.service;

import org.dromara.dh.domain.dto.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 数字人服务接口
 *
 * <p>负责与数字人服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface IDigitalHumanApiService {

    /**
     * 保存数字人配置
     *
     * @param request 配置请求（Java 驼峰格式）
     * @return 配置响应
     */
    Mono<DhConfigResponse> saveDigitalHumanConfig(DhConfigRequest request);


    /**
     * 删除数字人
     *
     * @param digitalHumanId 数字人ID
     * @return 删除响应
     */
    Mono<DigitalHumanDeleteResponse> deleteDigitalHuman(String digitalHumanId);

    /**
     * 启动训练任务
     *
     * @param request 训练请求
     * @return 训练响应
     */
    Mono<VideoUploadTrainResponse> uploadVideoAndTrain(VideoUploadTrainRequest request);

    /**
     * 查询训练进度
     *
     * @param taskId 任务ID
     * @return 训练进度响应
     */
    Mono<TrainingProgressResponse> getTrainingProgress(String taskId);

    /**
     * 获取数字人形象列表
     *
     * @return 形象信息列表
     */
    Mono<List<AvatarInfo>> getAvatars();

    /**
     * TTS 音色试听
     *
     * @param request 试听请求
     * @return 试听响应（含 base64 音频）
     */
    Mono<PreviewTtsResponse> previewTts(PreviewTtsRequest request);

}
