package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 义诊筛查批次业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "筛查批次业务对象")
@AutoMapper(target = ChScreeningBatch.class, reverseConvertGenerate = false)
public class ChScreeningBatchBo extends BaseEntity {

    @Schema(description = "批次ID")
    private Long batchId;

    @Schema(description = "筛查批次名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "筛查批次名称不能为空")
    private String batchName;

    @Schema(description = "活动日期")
    private Date activityDate;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "医生用户ID")
    private Long doctorUserId;

    @Schema(description = "活动地点")
    private String location;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "状态")
    private String status;
}
