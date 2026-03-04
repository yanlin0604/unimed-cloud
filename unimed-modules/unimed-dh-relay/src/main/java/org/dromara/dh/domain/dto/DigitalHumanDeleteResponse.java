package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数字人删除响应
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人删除响应")
public class DigitalHumanDeleteResponse {

    @Schema(description = "删除是否成功", example = "true")
    private Boolean success;

    @Schema(description = "删除结果消息", example = "数字人删除成功")
    private String message;

    @Schema(description = "训练服务删除结果")
    private TrainingServiceDeleteResult trainingServiceResult;

    /**
     * 数字人服务删除结果
     */
    @Data
    @Schema(description = "数字人服务删除结果")
    public static class DigitalServiceDeleteResult {
        @Schema(description = "响应码", example = "200")
        private Integer code;

        @Schema(description = "响应消息", example = "操作成功")
        private String msg;
    }

    /**
     * 训练服务删除结果
     */
    @Data
    @Schema(description = "训练服务删除结果")
    public static class TrainingServiceDeleteResult {
        @Schema(description = "删除是否成功", example = "true")
        private Boolean success;

        @Schema(description = "响应消息", example = "任务已删除")
        private String message;

        @Schema(description = "任务ID", example = "4486675750304")
        private String taskId;
    }
}
