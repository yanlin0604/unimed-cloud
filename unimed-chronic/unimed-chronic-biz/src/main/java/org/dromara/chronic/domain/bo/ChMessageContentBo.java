package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChMessageContent;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息内容业务对象")
@AutoMapper(target = ChMessageContent.class, reverseConvertGenerate = false)
public class ChMessageContentBo extends BaseEntity {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    @Schema(description = "发送者类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "发送者类型不能为空")
    private String senderType;

    @Schema(description = "内容类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容类型不能为空")
    private String contentType;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "语音时长")
    private Integer voiceDuration;

}
