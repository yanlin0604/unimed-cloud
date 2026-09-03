package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChTumorRecord;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 肿瘤专病专项档案视图对象
 *
 * @author unimed
 */
@Schema(description = "肿瘤专病专项档案视图对象")
@Data
@AutoMapper(target = ChTumorRecord.class)
public class ChTumorRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "患者ID")
    private Long patientId;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
