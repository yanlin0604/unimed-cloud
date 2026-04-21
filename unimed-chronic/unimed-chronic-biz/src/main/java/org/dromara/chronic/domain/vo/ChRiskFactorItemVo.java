package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChRiskFactorItem;

import java.io.Serial;
import java.io.Serializable;

/**
 * 危险因子视图对象
 *
 * @author unimed
 */
@Schema(description = "风险因子项视图对象")
@Data
@AutoMapper(target = ChRiskFactorItem.class)
public class ChRiskFactorItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "评估ID")
    private Long assessmentId;
    @Schema(description = "因子名称")
    private String factorName;
    @Schema(description = "因子值")
    private String factorValue;
    @Schema(description = "因子权重")
    private Integer factorWeight;
}
