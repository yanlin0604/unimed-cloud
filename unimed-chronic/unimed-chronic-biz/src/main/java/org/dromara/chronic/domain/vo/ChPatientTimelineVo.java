package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者时间线视图对象 ch_patient_timeline
 *
 * @author unimed
 */
@Schema(description = "患者时间线视图对象")
@Data
@AutoMapper(target = ChPatientTimeline.class)
public class ChPatientTimelineVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "事件标题")
    private String eventTitle;

    @Schema(description = "事件详情")
    private String eventDetail;

    @Schema(description = "事件时间")
    private Date eventTime;

    @Schema(description = "事件类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "eventType", other = ChronicDictTypeConstant.CHRONIC_EVENT_TYPE)
    private String eventTypeName;
}
