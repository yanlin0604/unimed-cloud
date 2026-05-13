package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * eGFR 计算结果视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "eGFR 计算结果")
public class EgfrCalcVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "eGFR 值（mL/min/1.73m²）")
    private BigDecimal egfrValue;

    @Schema(description = "CKD 分期：G1/G2/G3a/G3b/G4/G5")
    private String ckdStage;

    @Schema(description = "分期描述")
    private String stageDescription;
}
