package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 随访计划项业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "随访计划项业务对象")
@AutoMapper(target = ChFollowupPlanItem.class, reverseConvertGenerate = false)
public class ChFollowupPlanItemBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "计划ID")
    private Long planId;

    @Schema(description = "随访计划项类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "随访计划项类型不能为空")
    private String itemType;

    @Schema(description = "随访计划项配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "随访计划项配置不能为空")
    private String itemConfig;
}
