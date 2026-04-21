package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 患者病种业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者病种业务对象")
@AutoMapper(target = ChPatientDisease.class, reverseConvertGenerate = false)
public class ChPatientDiseaseBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    @Schema(description = "ICD编码")
    private String icdCode;

    @Schema(description = "诊断依据")
    private String diagnosisBasis;

    @Schema(description = "确诊日期")
    private Date confirmDate;

    @Schema(description = "是否并发症")
    private Boolean isComplication;

    @Schema(description = "父级病种编码")
    private String parentDiseaseCode;

    @Schema(description = "机构ID")
    private Long orgId;
}
