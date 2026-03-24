package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统音色新增/编辑 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "系统音色新增/编辑参数")
public class DhVoiceBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音色ID（编辑时必填）
     */
    @Schema(description = "音色ID，编辑时必填")
    private Long voiceId;

    /**
     * 音色名称
     */
    @NotBlank(message = "音色名称不能为空")
    @Schema(description = "音色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * OSS文件ID
     */
    @Schema(description = "OSS文件ID")
    private String ossId;

    /**
     * 试听音频URL
     */
    @Schema(description = "试听音频URL")
    private String sampleUrl;

    /**
     * 排序号
     */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
