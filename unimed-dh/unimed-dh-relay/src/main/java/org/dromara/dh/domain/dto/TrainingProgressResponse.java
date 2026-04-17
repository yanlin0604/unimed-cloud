package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 训练进度查询响应
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "训练进度查询响应")
public class TrainingProgressResponse {

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "视频名称")
    private String videoName;

    @Schema(description = "视频URL")
    private String videoUrl;

    @Schema(description = "训练类型")
    private String trainType;

    @Schema(description = "状态", example = "training")
    private String status;

    @Schema(description = "进度百分比", example = "40")
    private Integer progress;

    @Schema(description = "状态消息")
    private String message;

    @Schema(description = "错误信息")
    private String error;

    @Schema(description = "开始时间")
    private Double startTime;

    @Schema(description = "结束时间")
    private Double endTime;

    @Schema(description = "持续时间")
    private Double duration;

    @Schema(description = "是否为URL视频")
    private Boolean isUrlVideo;
}