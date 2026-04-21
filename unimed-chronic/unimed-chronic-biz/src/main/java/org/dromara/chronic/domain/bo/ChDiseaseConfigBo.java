package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDiseaseConfig;
import org.dromara.common.core.xss.Xss;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 慢病病种配置业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "慢病病种配置业务对象")
@AutoMapper(target = ChDiseaseConfig.class, reverseConvertGenerate = false)
public class ChDiseaseConfigBo extends BaseEntity {

    @Schema(description = "配置ID")
    private Long configId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Xss(message = "病种编码不能包含脚本字符")
    @NotBlank(message = "病种编码不能为空")
    @Size(max = 64, message = "病种编码长度不能超过{max}个字符")
    private String diseaseCode;

    @Schema(description = "病种名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Xss(message = "病种名称不能包含脚本字符")
    @NotBlank(message = "病种名称不能为空")
    @Size(max = 128, message = "病种名称长度不能超过{max}个字符")
    private String diseaseName;

    @Schema(description = "病种分类")
    private String diseaseCategory;

    @Schema(description = "是否主病种")
    private Boolean isPrimary;

    @Schema(description = "父级病种编码")
    private String parentDiseaseCode;

    @Schema(description = "随访模板ID")
    private Long followupTemplateId;

    @Schema(description = "评估策略ID")
    private Long assessmentStrategyId;

    @Schema(description = "监测项目")
    private String monitorItems;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
