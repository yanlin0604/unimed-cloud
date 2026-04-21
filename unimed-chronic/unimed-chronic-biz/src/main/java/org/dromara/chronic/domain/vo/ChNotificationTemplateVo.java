package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChNotificationTemplate;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "通知模板视图对象")
@Data
@AutoMapper(target = ChNotificationTemplate.class)
public class ChNotificationTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板内容")
    private String templateContent;

}
