package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChRiskAssessment;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 风险评估业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "风险评估业务对象")
@AutoMapper(target = ChRiskAssessment.class, reverseConvertGenerate = false)
public class ChRiskAssessmentBo extends BaseEntity {

    @Schema(description = "评估ID")
    private Long assessmentId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    /**
     * 指标数据 JSON，例如 {\"systolic\":160,\"glucose\":8.2}
     */
    @Schema(description = "指标数据 JSON，例如 {\"systolic\":160,\"glucose\":8.2}")
    private String metricData;

    /**
     * 因子数据 JSON，例如 {\"smoking\":true,\"familyHistory\":true}
     */
    @Schema(description = "因子数据 JSON，例如 {\"smoking\":true,\"familyHistory\":true}")
    private String factorData;

    @Schema(description = "评估人ID")
    private Long assessorUserId;

    @Schema(description = "机构ID")
    private Long orgId;
}
