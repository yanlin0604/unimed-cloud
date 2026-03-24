package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统素材新增/编辑 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "系统素材新增/编辑参数")
public class DhMaterialBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 素材ID（编辑时必填）
     */
    @Schema(description = "素材ID，编辑时必填")
    private Long materialId;

    /**
     * 素材名称
     */
    @NotBlank(message = "素材名称不能为空")
    @Schema(description = "素材名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 素材类型 IMAGE/VIDEO/AUDIO
     */
    @NotBlank(message = "素材类型不能为空")
    @Schema(description = "素材类型 IMAGE/VIDEO/AUDIO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialType;

    /**
     * OSS文件ID
     */
    @Schema(description = "OSS文件ID")
    private String ossId;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /**
     * 缩略图URL
     */
    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名")
    private String fileName;

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
