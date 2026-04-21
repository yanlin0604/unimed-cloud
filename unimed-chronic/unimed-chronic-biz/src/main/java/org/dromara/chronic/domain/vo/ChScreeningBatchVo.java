package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChScreeningBatch;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 义诊筛查批次视图对象
 *
 * @author unimed
 */
@Schema(description = "筛查批次视图对象")
@Data
@AutoMapper(target = ChScreeningBatch.class)
public class ChScreeningBatchVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "批次ID")
    private Long batchId;
    @Schema(description = "批次名称")
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
