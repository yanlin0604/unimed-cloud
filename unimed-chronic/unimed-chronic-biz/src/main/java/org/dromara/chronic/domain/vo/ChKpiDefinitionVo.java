package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChKpiDefinition;

import java.io.Serial;
import java.io.Serializable;

/**
 * KPI指标视图对象
 *
 * @author unimed
 */
@Schema(description = "KPI指标定义视图对象")
@Data
@AutoMapper(target = ChKpiDefinition.class)
public class ChKpiDefinitionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "KPI ID")
    private Long kpiId;
    @Schema(description = "KPI编码")
    private String kpiCode;
    @Schema(description = "KPI名称")
    private String kpiName;
    @Schema(description = "KPI公式")
    private String kpiFormula;
    @Schema(description = "KPI分类")
    private String kpiCategory;
}
