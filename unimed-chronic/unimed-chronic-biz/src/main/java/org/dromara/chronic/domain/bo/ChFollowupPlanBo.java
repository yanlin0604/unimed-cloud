package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.List;

/**
 * 随访计划业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "随访计划业务对象")
@AutoMapper(target = ChFollowupPlan.class, reverseConvertGenerate = false)
public class ChFollowupPlanBo extends BaseEntity {

    @Schema(description = "计划ID")
    private Long planId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    @Schema(description = "随访周期(天)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "随访周期不能为空")
    private Integer cycleDays;

    @Schema(description = "总轮次", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总轮次不能为空")
    private Integer totalRounds;

    @Schema(description = "计划状态")
    private String planStatus;

    @Schema(description = "执行医生用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "执行医生用户ID不能为空")
    private Long assigneeUserId;

    @Valid
    @Schema(description = "随访计划项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "随访计划项列表不能为空")
    private List<ChFollowupPlanItemBo> itemList;
}
