package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 训练进度查询请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "训练进度查询请求")
public class TrainingProgressRequest {

    @Schema(description = "任务ID", example = "9028904922969")
    @NotBlank(message = "任务ID不能为空")
    private String taskId;
}