package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChManageLevelRecord;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理级别变更视图对象
 *
 * @author unimed
 */
@Schema(description = "管理等级变更视图对象")
@Data
@AutoMapper(target = ChManageLevelRecord.class)
public class ChManageLevelRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "原管理等级")
    private String oldLevel;
    @Schema(description = "新管理等级")
    private String newLevel;
    @Schema(description = "变更原因")
    private String changeReason;
    @Schema(description = "变更时间")
    private Date changeTime;
}
