package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 医疗文档OCR确认业务对象
 *
 * @author unimed
 */
@Data
@Schema(description = "医疗文档OCR确认业务对象")
public class OcrConfirmBo {

    @Schema(description = "确认目标: ARCHIVE/METRIC/REPORT/MIXED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "确认目标不能为空")
    private String confirmTarget;

    @Schema(description = "患者建档草稿")
    @Valid
    private ChPatientProfileBo profile;

    @Schema(description = "患者病种草稿")
    @Valid
    private List<ChPatientDiseaseBo> diseases;

    @Schema(description = "是否更新已存在患者")
    private Boolean updateSupport;

    @Schema(description = "健康指标草稿")
    @Valid
    private List<ChHealthMetricRecordBo> metrics;

    @Schema(description = "检查检验报告草稿")
    @Valid
    private ChHealthExamBo exam;

    @Schema(description = "检查检验报告项目草稿")
    @Valid
    private List<ChHealthExamItemBo> reportItems;

    @Schema(description = "备注")
    private String remark;
}
