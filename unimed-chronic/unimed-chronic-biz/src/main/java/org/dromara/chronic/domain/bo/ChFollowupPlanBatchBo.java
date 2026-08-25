package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 随访计划批量创建业务对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访计划批量创建业务对象")
public class ChFollowupPlanBatchBo {

    @Schema(description = "患者ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "患者列表不能为空")
    private List<@NotNull(message = "患者ID不能为空") Long> patientIds;

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

    @Schema(description = "执行人用户ID（为空时自动进入随访任务池）")
    private Long assigneeUserId;

    @Valid
    @Schema(description = "随访计划项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "随访计划项列表不能为空")
    private List<ChFollowupPlanItemBo> itemList;
}
