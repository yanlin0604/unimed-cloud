package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 随访问卷模板业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "随访问卷业务对象")
@AutoMapper(target = ChFollowupQuestionnaire.class, reverseConvertGenerate = false)
public class ChFollowupQuestionnaireBo extends BaseEntity {

    @Schema(description = "问卷ID")
    private Long questionnaireId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    @Schema(description = "问卷名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问卷名称不能为空")
    private String questionnaireName;

    @Schema(description = "版本号")
    private Integer version;

    /**
     * 问卷题目 JSON（含跳题逻辑 skip_logic）
     */
    @Schema(description = "问卷题目", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问卷题目不能为空")
    private String questions;

    /**
     * 是否国家公卫标准模板
     */
    @Schema(description = "是否国家公卫标准模板")
    private Boolean isNationalStandard;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}
