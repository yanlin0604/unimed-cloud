package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频上传和训练响应
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "视频上传和训练响应")
public class VideoUploadTrainResponse {

    @Schema(description = "上传服务响应")
    private UploadServiceResult uploadResult;

    @Schema(description = "训练服务响应")
    private TrainingServiceResult trainingResult;

    @Schema(description = "整体操作是否成功")
    private Boolean success;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "数字人ID")
    private String digitalId;

    @Schema(description = "任务ID")
    private String taskId;

    /**
     * 上传服务结果
     */
    @Data
    @Schema(description = "上传服务结果")
    public static class UploadServiceResult {
        @Schema(description = "响应消息")
        private String msg;

        @Schema(description = "响应码")
        private Integer code;

        @Schema(description = "数字人ID")
        private String data;
    }

    /**
     * 训练服务结果
     */
    @Data
    @Schema(description = "训练服务结果")
    public static class TrainingServiceResult {
        @Schema(description = "是否成功")
        private Boolean success;

        @Schema(description = "响应消息")
        private String message;

        @Schema(description = "任务ID")
        private String taskId;

        @Schema(description = "状态")
        private String status;
    }
}