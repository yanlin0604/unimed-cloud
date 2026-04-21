package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChMedicationAdjust;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用药调整视图对象
 *
 * @author unimed
 */
@Schema(description = "用药调整视图对象")
@Data
@AutoMapper(target = ChMedicationAdjust.class)
public class ChMedicationAdjustVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "用药记录ID")
    private Long medId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "调整类型")
    private String adjustType;
    @Schema(description = "调整原因")
    private String adjustReason;
    @Schema(description = "不良反应")
    private String adverseReaction;
    @Schema(description = "预览已确认")
    private Boolean previewConfirmed;
    @Schema(description = "PIN验证时间")
    private Date pinVerifiedAt;
    @Schema(description = "调整人用户ID")
    private Long adjusterUserId;
}
