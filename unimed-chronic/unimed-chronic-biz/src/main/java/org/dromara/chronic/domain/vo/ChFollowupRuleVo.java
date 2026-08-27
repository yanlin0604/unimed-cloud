package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFollowupRule;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 慢病随访排期规则配置视图对象
 *
 * @author unimed
 */
@Schema(description = "随访排期规则视图对象")
@Data
@AutoMapper(target = ChFollowupRule.class)
public class ChFollowupRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID")
    private Long id;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "风险/管理等级")
    private String riskLevel;
    @Schema(description = "随访周期(天)")
    private Integer cycleDays;
    @Schema(description = "总轮次")
    private Integer totalRounds;
    @Schema(description = "首轮到期天数")
    private Integer firstDueDays;
    @Schema(description = "默认随访方式")
    private String defaultVisitType;
    @Schema(description = "方案建议文案")
    private String summaryAdvice;
    @Schema(description = "是否启用")
    private Boolean isActive;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "病种名称")
    private String diseaseName;
}