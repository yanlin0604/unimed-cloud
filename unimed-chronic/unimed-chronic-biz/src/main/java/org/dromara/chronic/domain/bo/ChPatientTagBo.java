package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 患者标签业务对象 ch_patient_tag
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者标签业务对象")
@AutoMapper(target = ChPatientTag.class, reverseConvertGenerate = false)
public class ChPatientTagBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "标签类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签类型不能为空")
    private String tagType;

    @Schema(description = "标签值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签值不能为空")
    private String tagValue;
}
