package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDrugInteraction;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 药物相互作用业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "药物相互作用业务对象")
@AutoMapper(target = ChDrugInteraction.class, reverseConvertGenerate = false)
public class ChDrugInteractionBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "药物A编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "药物A编码不能为空")
    private String drugCodeA;

    @Schema(description = "药物B编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "药物B编码不能为空")
    private String drugCodeB;

    @Schema(description = "冲突级别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "冲突级别不能为空")
    private String interactionLevel;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "临床建议")
    private String clinicalAdvice;
}
