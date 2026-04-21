package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChWarningRule;

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
    @Schema(description = "机构ID")
    private Long orgId;
    @Schema(description = "创建时间")
    private Date createTime;
}
