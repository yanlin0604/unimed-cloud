package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChHealthEducationDelivery;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Schema(description = "宣教投递视图对象")
@Data
@AutoMapper(target = ChHealthEducationDelivery.class)
public class ChHealthEducationDeliveryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "投递ID")
    private Long deliveryId;

    @Schema(description = "宣教内容ID")
    private Long contentId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "触发类型")
    private String triggerType;

    @Schema(description = "推送渠道")
    private String pushChannel;

    @Schema(description = "投递状态")
    private String deliveryStatus;

    @Schema(description = "阅读状态")
    private Boolean readStatus;

    @Schema(description = "阅读时间")
    private Date readTime;

    @Schema(description = "停留时长")
    private Integer stayDuration;

    @Schema(description = "触发类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "triggerType", other = ChronicDictTypeConstant.CHRONIC_TRIGGER_TYPE)
    private String triggerTypeName;

    @Schema(description = "推送渠道名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "pushChannel", other = ChronicDictTypeConstant.CHRONIC_PUSH_CHANNEL)
    private String pushChannelName;

    @Schema(description = "推送状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "deliveryStatus", other = ChronicDictTypeConstant.CHRONIC_DELIVERY_STATUS)
    private String deliveryStatusName;

    @Schema(description = "宣教内容标题（按 contentId 回填）")
    private String contentTitle;

    @Schema(description = "创建时间")
    private Date createTime;

}
