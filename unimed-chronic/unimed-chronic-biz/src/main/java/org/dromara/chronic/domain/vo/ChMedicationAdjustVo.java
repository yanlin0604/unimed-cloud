package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChMedicationAdjust;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

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

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "调整类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "adjustType", other = ChronicDictTypeConstant.CHRONIC_ADJUST_TYPE)
    private String adjustTypeName;

    @Schema(description = "调整人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "adjusterUserId")
    private String adjusterNickName;
}
