package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChMessageContent;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "消息内容视图对象")
@Data
@AutoMapper(target = ChMessageContent.class)
public class ChMessageContentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "会话ID")
    private Long sessionId;

    @Schema(description = "发送者类型")
    private String senderType;

    @Schema(description = "内容类型")
    private String contentType;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "语音时长")
    private Integer voiceDuration;

}
