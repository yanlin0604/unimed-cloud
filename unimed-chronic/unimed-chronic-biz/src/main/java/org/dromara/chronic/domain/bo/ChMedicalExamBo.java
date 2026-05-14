package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChMedicalExam;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 检查记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "检查记录业务对象")
@AutoMapper(target = ChMedicalExam.class, reverseConvertGenerate = false)
public class ChMedicalExamBo extends BaseEntity {

    @Schema(description = "检查ID")
    private Long examId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "检查日期")
    private Date examDate;

    @Schema(description = "检查类型")
    private String examType;

    @Schema(description = "检查部位")
    private String examPart;

    @Schema(description = "检查结果描述")
    private String examResult;

    @Schema(description = "检查结论")
    private String examConclusion;

    @Schema(description = "报告图片URL")
    private String reportImage;

    @Schema(description = "检查医院")
    private String hospital;

    @Schema(description = "检查医生")
    private String doctor;

    @Schema(description = "备注")
    private String remark;
}
