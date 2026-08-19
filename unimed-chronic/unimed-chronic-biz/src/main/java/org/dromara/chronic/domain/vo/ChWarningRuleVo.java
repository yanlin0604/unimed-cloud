package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预警规则视图对象
 *
 * @author unimed
 */
@Schema(description = "预警规则视图对象")
@Data
@AutoMapper(target = ChWarningRule.class)
public class ChWarningRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID")
    private Long ruleId;
    @Schema(description = "规则名称")
    private String ruleName;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "指标类型")
    private String metricType;
    @Schema(description = "预警级别")
    private String warningLevel;
    @Schema(description = "阈值下限")
    private BigDecimal thresholdMin;
    @Schema(description = "阈值上限")
    private BigDecimal thresholdMax;
    @Schema(description = "连续窗口")
    private Integer consecutiveWindow;
    @Schema(description = "时间窗口开始")
    private Date timeWindowStart;
    @Schema(description = "时间窗口结束")
    private Date timeWindowEnd;
    @Schema(description = "恢复规则")
    private String recoveryRule;
    @Schema(description = "规则描述")
    private String description;
    @Schema(description = "临床处置建议（命中规则后给医生的具体处置指引）")
    private String clinicalAdvice;
    @Schema(description = "要求响应时限(小时)，危急值通常为 2，一般高值为 24")
    private Integer responseSlaHours;
    @Schema(description = "机构ID（对应系统库 sys_dept.dept_id）")
    private Long orgId;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "预警等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "warningLevel", other = ChronicDictTypeConstant.CHRONIC_WARNING_LEVEL)
    private String warningLevelName;

    @Schema(description = "指标类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "metricType", other = ChronicDictTypeConstant.CHRONIC_METRIC_TYPE)
    private String metricTypeName;

    @Schema(description = "病种名称")
    private String diseaseName;

    @Schema(description = "机构名称（按 orgId 查 sys_dept 回填）")
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "orgId")
    private String orgName;
}
