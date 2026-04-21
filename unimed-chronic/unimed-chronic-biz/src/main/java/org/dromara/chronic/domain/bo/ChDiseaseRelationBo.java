package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDiseaseRelation;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 病种关系业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "病种关系业务对象")
@AutoMapper(target = ChDiseaseRelation.class, reverseConvertGenerate = false)
public class ChDiseaseRelationBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "主病编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "主病编码不能为空")
    private String parentDiseaseCode;

    @Schema(description = "并发症病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "并发症病种编码不能为空")
    private String complicationDiseaseCode;

    @Schema(description = "关系类型")
    private String relationType;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
