package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访记录视图对象
 *
 * @author unimed
 */
@Schema(description = "随访记录视图对象")
@Data
@AutoMapper(target = ChFollowupRecord.class)
public class ChFollowupRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long recordId;
    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "随访方式")
    private String visitType;
    @Schema(description = "随访内容")
    private String visitContent;
    @Schema(description = "随访人用户ID")
    private Long visitorUserId;
    @Schema(description = "随访日期")
    private Date visitDate;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "结构化随访内容")
    private Map<String, Object> content;

    @Schema(description = "问卷答案")
    private List<ChFollowupAnswerVo> answers;

    @Schema(description = "随访方式名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "visitType", other = ChronicDictTypeConstant.CHRONIC_VISIT_TYPE)
    private String visitTypeName;

    @Schema(description = "随访人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "visitorUserId")
    private String visitorNickName;
}
