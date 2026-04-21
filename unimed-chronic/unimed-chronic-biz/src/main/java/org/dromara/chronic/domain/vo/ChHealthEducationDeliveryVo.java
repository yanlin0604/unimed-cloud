package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChHealthEducationDelivery;

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

}
