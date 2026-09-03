package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChAiCallTask;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI智能随访外呼任务视图对象
 *
 * @author unimed
 */
@Schema(description = "AI智能随访外呼任务视图对象")
@Data
@AutoMapper(target = ChAiCallTask.class)
public class ChAiCallTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "外呼任务ID")
    private Long taskId;

    @Schema(description = "随访计划ID")
    private Long planId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "外呼电话")
    private String patientPhone;

    @Schema(description = "专病类型")
    private String diseaseCode;

    @Schema(description = "优先级(1-5)")
    private Integer callPriority;

    @Schema(description = "状态(PENDING/CALLING/SUCCESS/FAILED/REFUSED)")
    private String callStatus;

    @Schema(description = "录音文件URL")
    private String audioRecordUrl;

    @Schema(description = "语音转写文本")
    private String transcriptText;

    @Schema(description = "抽取指标JSON")
    private String extractedMetrics;

    @Schema(description = "患者反馈主诉")
    private String patientFeedback;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
