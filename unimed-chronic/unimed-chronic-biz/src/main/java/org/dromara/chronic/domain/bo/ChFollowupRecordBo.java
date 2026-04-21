package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 随访记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "随访记录业务对象")
@AutoMapper(target = ChFollowupRecord.class, reverseConvertGenerate = false)
public class ChFollowupRecordBo extends BaseEntity {

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "随访方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "随访方式不能为空")
    private String visitType;

    @Schema(description = "随访内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "随访内容不能为空")
    private String visitContent;

    @Schema(description = "随访人ID")
    private Long visitorUserId;
}
