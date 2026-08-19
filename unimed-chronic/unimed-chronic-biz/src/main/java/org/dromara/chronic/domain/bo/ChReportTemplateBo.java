package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChReportTemplate;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 报告模板业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报告模板业务对象")
@AutoMapper(target = ChReportTemplate.class, reverseConvertGenerate = false)
public class ChReportTemplateBo extends BaseEntity {

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "模板内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板内容不能为空")
    private String templateContent;

    @Schema(description = "病种编码")
    private String diseaseCode;

    @Schema(description = "模板类型(ANNUAL/FOLLOWUP/SPECIAL)")
    private String templateType;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
