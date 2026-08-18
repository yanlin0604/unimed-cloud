package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChNotificationTemplate;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Schema(description = "通知模板视图对象")
@Data
@AutoMapper(target = ChNotificationTemplate.class)
public class ChNotificationTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "渠道名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "channel", other = ChronicDictTypeConstant.CHRONIC_PUSH_CHANNEL)
    private String channelName;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板内容")
    private String templateContent;

    @Schema(description = "是否启用: 1启用 0停用")
    private String isActive;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

}
