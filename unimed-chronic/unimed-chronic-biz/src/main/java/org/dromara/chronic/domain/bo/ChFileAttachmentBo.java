package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFileAttachment;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 附件业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件附件业务对象")
@AutoMapper(target = ChFileAttachment.class, reverseConvertGenerate = false)
public class ChFileAttachmentBo extends BaseEntity {

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    @Schema(description = "业务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "业务ID不能为空")
    private Long bizId;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "OSS ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "OSS ID不能为空")
    private Long ossId;
}
