package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupAnswer;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 随访问卷作答业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "问卷答案业务对象")
@AutoMapper(target = ChFollowupAnswer.class, reverseConvertGenerate = false)
public class ChFollowupAnswerBo extends BaseEntity {

    @Schema(description = "随访记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "随访记录ID不能为空")
    private Long recordId;

    @Schema(description = "问卷ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目ID不能为空")
    private String questionId;

    @Schema(description = "作答内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "作答内容不能为空")
    private String answerValue;
}
