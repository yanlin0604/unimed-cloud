package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChManagePlanItem;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 管理方案子项视图对象
 *
 * @author unimed
 */
@Schema(description = "管理方案项视图对象")
@Data
@AutoMapper(target = ChManagePlanItem.class)
public class ChManagePlanItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "项类型")
    private String itemType;
    @Schema(description = "项内容")
    private String itemContent;
    @Schema(description = "目标指标类型(如 BP_SYSTOLIC)")
    private String targetMetricType;
    @Schema(description = "目标下限值")
    private BigDecimal targetMinValue;
    @Schema(description = "目标上限值")
    private BigDecimal targetMaxValue;
    @Schema(description = "项类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "itemType", other = ChronicDictTypeConstant.CHRONIC_PLAN_ITEM_TYPE)
    private String itemTypeName;
}
