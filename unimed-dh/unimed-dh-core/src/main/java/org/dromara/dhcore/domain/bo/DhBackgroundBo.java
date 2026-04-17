package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统背景新增/编辑 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "系统背景新增/编辑参数")
public class DhBackgroundBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 背景ID（编辑时必填）
     */
    @Schema(description = "背景ID，编辑时必填")
    private Long backgroundId;

    /**
     * 背景名称
     */
    @NotBlank(message = "背景名称不能为空")
    @Schema(description = "背景名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 背景类型 IMAGE/VIDEO
     */
    @NotBlank(message = "背景类型不能为空")
    @Schema(description = "背景类型 IMAGE/VIDEO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bgType;

    /**
     * OSS文件ID
     */
    @Schema(description = "OSS文件ID")
    private String ossId;

    /**
     * 预览URL
     */
    @Schema(description = "预览URL")
    private String previewUrl;

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
