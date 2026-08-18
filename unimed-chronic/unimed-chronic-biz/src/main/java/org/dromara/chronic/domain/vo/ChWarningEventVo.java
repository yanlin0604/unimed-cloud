package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 预警事件视图对象
 *
 * @author unimed
 */
@Schema(description = "预警事件视图对象")
@Data
@AutoMapper(target = ChWarningEvent.class)
public class ChWarningEventVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "预警ID")
    private Long warningId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "规则ID")
    private Long ruleId;
    @Schema(description = "预警级别")
    private String warningLevel;
    @Schema(description = "预警值（同指标值格式）")
    private String warningValue;
    @Schema(description = "预警时间")
    private Date warningTime;
    @Schema(description = "事件状态")
    private String eventStatus;
    @Schema(description = "指派用户ID")
    private Long assigneeUserId;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "处置动作列表")
    private List<ChWarningActionVo> actions;

    @Schema(description = "规则名称（取 ch_warning_rule.description，由 service 层回填）")
    private String ruleName;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "预警等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "warningLevel", other = ChronicDictTypeConstant.CHRONIC_WARNING_LEVEL)
    private String warningLevelName;

    @Schema(description = "事件状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "eventStatus", other = ChronicDictTypeConstant.CHRONIC_EVENT_STATUS)
    private String eventStatusName;

    @Schema(description = "处理人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "assigneeUserId")
    private String assigneeNickName;
}
