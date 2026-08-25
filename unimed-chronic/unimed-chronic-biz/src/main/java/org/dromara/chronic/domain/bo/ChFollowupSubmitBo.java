package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 随访执行提交对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访执行提交对象")
public class ChFollowupSubmitBo {

    @Schema(description = "随访摘要", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "随访摘要不能为空")
    private String visitContent;

    @Schema(description = "问卷ID")
    private Long questionnaireId;

    @Valid
    @Schema(description = "问卷答案")
    private List<ChFollowupAnswerInputBo> answers;

    @Schema(description = "生命体征（包含血压收缩压/舒张压、血糖、心率、BMI等）")
    private Map<String, Object> vitalSigns;

    @Schema(description = "用药情况")
    private Map<String, Object> medicationStatus;

    @Schema(description = "依从性评价")
    private Map<String, Object> adherence;

    @Schema(description = "生活方式")
    private Map<String, Object> lifestyle;

    @Schema(description = "康复情况")
    private Map<String, Object> rehabilitationStatus;

    @Schema(description = "随访结论(CONTROLLED-控制良好/IMPROVING-好转/UNCONTROLLED-控制不良/DETERIORATING-恶化/REFERRAL-建议转诊)")
    private String followupResult;

    @Schema(description = "康复评级(EXCELLENT-优秀/GOOD-良好/FAIR-一般/POOR-较差)")
    private String rehabLevel;

    @Schema(description = "随访建议")
    private String advice;

    @Schema(description = "随访回报与健康指导建议")
    private String feedbackAdvice;

    @Schema(description = "下次随访日期，格式 yyyy-MM-dd")
    private String nextFollowupDate;
}
