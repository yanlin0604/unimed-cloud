package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChRiskAssessment;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 风险评估视图对象
 *
 * @author unimed
 */
@Schema(description = "风险评估视图对象")
@Data
@AutoMapper(target = ChRiskAssessment.class)
public class ChRiskAssessmentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "评估ID")
    private Long assessmentId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "风险等级")
    private String riskLevel;
    @Schema(description = "评估报告")
    private String assessmentReport;
    @Schema(description = "评估人用户ID")
    private Long assessorUserId;
    @Schema(description = "机构ID")
    private Long orgId;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "风险因子项列表")
    private List<ChRiskFactorItemVo> factorItems;
}
