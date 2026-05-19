package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChManagePlanItem;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 管理方案子项业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理方案项业务对象")
@AutoMapper(target = ChManagePlanItem.class, reverseConvertGenerate = false)
public class ChManagePlanItemBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "方案ID")
    private Long planId;

    @Schema(description = "方案子项类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "方案子项类型不能为空")
    private String itemType;

    @Schema(description = "方案子项内容(JSON)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "方案子项内容不能为空")
    private String itemContent;

    @Schema(description = "目标指标类型(如 BP_SYSTOLIC)")
    private String targetMetricType;

    @Schema(description = "目标下限值")
    private BigDecimal targetMinValue;

    @Schema(description = "目标上限值")
    private BigDecimal targetMaxValue;
}
