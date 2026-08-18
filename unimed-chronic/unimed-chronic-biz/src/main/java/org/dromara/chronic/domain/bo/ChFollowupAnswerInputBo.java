package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 随访问卷答案输入对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访问卷答案输入对象")
public class ChFollowupAnswerInputBo {

    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目ID不能为空")
    private String questionId;

    @Schema(description = "答案值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "答案值不能为空")
    private String answerValue;
}
