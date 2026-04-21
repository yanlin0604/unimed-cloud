package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFileAttachment;

import java.io.Serial;
import java.io.Serializable;

/**
 * 附件视图对象
 *
 * @author unimed
 */
@Schema(description = "文件附件视图对象")
@Data
@AutoMapper(target = ChFileAttachment.class)
public class ChFileAttachmentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "OSS文件ID")
    private Long ossId;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "创建者")
    private Long createBy;

    @Schema(description = "创建时间")
    private java.util.Date createTime;
}
