package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChMessageSession;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(description = "消息会话视图对象")
@Data
@AutoMapper(target = ChMessageSession.class)
public class ChMessageSessionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "会话ID")
    private Long sessionId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "医生用户ID")
    private Long doctorUserId;

    @Schema(description = "会话类型")
    private String sessionType;

    @Schema(description = "最后消息时间")
    private Date lastMessageTime;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "最近消息列表")
    private List<ChMessageContentVo> recentMessages;

}
