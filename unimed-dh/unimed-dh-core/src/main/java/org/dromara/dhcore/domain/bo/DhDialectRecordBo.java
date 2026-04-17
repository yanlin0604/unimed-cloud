package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言采集录音提交 BO（C端）
 *
 * @author unimed
 */
@Data
@Schema(description = "方言录音提交参数")
public class DhDialectRecordBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联提示文字ID
     */
    @NotNull(message = "提示文字ID不能为空")
    @Schema(description = "提示文字ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long promptId;

    /**
     * 方言名称
     */
    @NotBlank(message = "方言名称不能为空")
    @Schema(description = "方言名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dialectName;

    /**
     * 方言地区
     */
    @Schema(description = "方言地区")
    private String dialectArea;

    /**
     * OSS文件ID
     */
    @NotBlank(message = "OSS文件ID不能为空")
    @Schema(description = "OSS文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ossId;

    /**
     * 录音时长（秒）
     */
    @Schema(description = "录音时长（秒）")
    private Integer duration;

    /**
     * 邀请码（可选，用于来源追踪）
     */
    @Schema(description = "邀请码")
    private String inviteCode;
}
