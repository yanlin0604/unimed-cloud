package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * eGFR 计算业务对象
 *
 * @author unimed
 */
@Data
@Schema(description = "eGFR 计算入参")
public class EgfrCalcBo {

    @Schema(description = "患者ID（提供则自动取年龄/性别）")
    private Long patientId;

    @Schema(description = "血清肌酐值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "肌酐值不能为空")
    private BigDecimal creatinine;

    @Schema(description = "肌酐单位 MG_DL / UMOL_L", defaultValue = "MG_DL")
    private String unit;

    @Schema(description = "年龄（无 patientId 时必填）")
    private Integer age;

    @Schema(description = "性别 0女 1男 2未知（无 patientId 时必填）")
    private String gender;
}
