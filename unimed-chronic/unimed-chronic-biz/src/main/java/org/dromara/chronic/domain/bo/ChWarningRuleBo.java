package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 预警规则业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "预警规则业务对象")
@AutoMapper(target = ChWarningRule.class, reverseConvertGenerate = false)
public class ChWarningRuleBo extends BaseEntity {

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    @Schema(description = "指标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "指标类型不能为空")
    private String metricType;

    @Schema(description = "预警级别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "预警级别不能为空")
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
}
