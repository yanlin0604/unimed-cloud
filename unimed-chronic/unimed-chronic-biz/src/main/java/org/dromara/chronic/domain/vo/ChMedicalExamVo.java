package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChMedicalExam;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 检查记录视图对象
 *
 * @author unimed
 */
@Schema(description = "检查记录视图对象")
@Data
@AutoMapper(target = ChMedicalExam.class)
public class ChMedicalExamVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "检查ID")
    private Long examId;

    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "患者姓名")
    private String patientName;

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

    @Schema(description = "创建时间")
    private Date createTime;
}
