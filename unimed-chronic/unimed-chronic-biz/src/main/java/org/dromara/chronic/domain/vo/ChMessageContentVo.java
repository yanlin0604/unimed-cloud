package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChMessageContent;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

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

    @Schema(description = "文件ID(图片/语音消息存 OSS 文件ID)")
    private Long fileId;

    @Schema(description = "文件访问地址(IMAGE/VOICE 消息由 fileId 翻译)")
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "fileId")
    private String fileUrl;

    @Schema(description = "语音时长")
    private Integer voiceDuration;

    @Schema(description = "消息时间")
    private Date createTime;

    @Schema(description = "发送者类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "senderType", other = ChronicDictTypeConstant.CHRONIC_SENDER_TYPE)
    private String senderTypeName;

    @Schema(description = "内容类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "contentType", other = ChronicDictTypeConstant.CHRONIC_CONTENT_TYPE)
    private String contentTypeName;

}
