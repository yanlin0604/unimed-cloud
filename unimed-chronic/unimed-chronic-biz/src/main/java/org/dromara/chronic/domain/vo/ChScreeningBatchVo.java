package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

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
    @Schema(description = "医生用户ID")
    private Long doctorUserId;
    @Schema(description = "活动地点")
    private String location;
    @Schema(description = "备注")
    private String notes;
    @Schema(description = "状态")
    private String status;

    @Schema(description = "批次状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "status", other = ChronicDictTypeConstant.CHRONIC_SCREENING_STATUS)
    private String statusName;
    @Schema(description = "负责医生昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "doctorUserId")
    private String doctorNickName;

    /**
     * 批次内筛查记录数（由 service 层批量聚合回填，非 DB 列）
     */
    @Schema(description = "批次内筛查记录数")
    private Long recordCount;
}
