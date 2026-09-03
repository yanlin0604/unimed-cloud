package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.chronic.domain.entity.ChAiCallTask;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * AI智能随访外呼任务业务对象
 *
 * @author unimed
 */
@Schema(description = "AI智能随访外呼任务业务对象")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChAiCallTask.class, reverseConvertGenerate = false)
public class ChAiCallTaskBo extends BaseEntity {

    @Schema(description = "外呼任务ID")
    private Long taskId;

    @Schema(description = "随访计划ID")
    private Long planId;

    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private Long patientId;

    @NotBlank(message = "外呼电话不能为空")
    @Schema(description = "外呼电话")
    private String patientPhone;

    @NotBlank(message = "专病类型不能为空")
    @Schema(description = "专病类型")
    private String diseaseCode;

    @Schema(description = "优先级(1-5)")
    private Integer callPriority;

    @Schema(description = "状态")
    private String callStatus;

    @Schema(description = "录音文件URL")
    private String audioRecordUrl;

    @Schema(description = "语音转写文本")
    private String transcriptText;

    @Schema(description = "抽取指标JSON")
    private String extractedMetrics;

    @Schema(description = "患者反馈主诉")
    private String patientFeedback;
}
