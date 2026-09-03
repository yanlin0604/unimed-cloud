package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.chronic.domain.entity.ChTumorRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 肿瘤专病专项档案业务对象
 *
 * @author unimed
 */
@Schema(description = "肿瘤专病专项档案业务对象")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChTumorRecord.class, reverseConvertGenerate = false)
public class ChTumorRecordBo extends BaseEntity {

    @Schema(description = "主键")
    private Long id;

    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private Long patientId;

    @NotNull(message = "肿瘤类型不能为空")
    @Schema(description = "肿瘤类型")
    private String cancerType;

    @Schema(description = "TNM分期")
    private String tnmStage;

    @Schema(description = "病理诊断")
    private String pathologyResult;

    @Schema(description = "手术日期")
    private LocalDate surgeryDate;

    @Schema(description = "放化疗状态")
    private String chemoStatus;

    @Schema(description = "癌胚抗原CEA(ng/mL)")
    private BigDecimal ceaValue;

    @Schema(description = "甲胎蛋白AFP(ng/mL)")
    private BigDecimal afpValue;

    @Schema(description = "高危因素")
    private String highRiskFactors;

    @Schema(description = "下次复查日期")
    private LocalDate nextReviewDate;
}
