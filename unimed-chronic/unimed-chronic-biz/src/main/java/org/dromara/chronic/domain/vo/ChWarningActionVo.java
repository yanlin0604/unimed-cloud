package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChWarningAction;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 预警处置动作视图对象
 *
 * @author unimed
 */
@Schema(description = "预警处置视图对象")
@Data
@AutoMapper(target = ChWarningAction.class)
public class ChWarningActionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "预警ID")
    private Long warningId;
    @Schema(description = "处置类型")
    private String actionType;
    @Schema(description = "处置详情")
    private String actionDetail;
    @Schema(description = "处置人用户ID")
    private Long actionUserId;
    @Schema(description = "处置时间")
    private Date actionTime;

    @Schema(description = "处置类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "actionType", other = ChronicDictTypeConstant.CHRONIC_ACTION_TYPE)
    private String actionTypeName;

    @Schema(description = "处置人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "actionUserId")
    private String actionNickName;
}
