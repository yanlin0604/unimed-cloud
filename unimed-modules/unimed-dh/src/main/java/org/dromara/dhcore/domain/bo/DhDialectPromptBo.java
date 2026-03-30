package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言采集提示文字新增/编辑 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "提示文字新增/编辑参数")
public class DhDialectPromptBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示文字ID（编辑时必填）
     */
    @Schema(description = "提示文字ID，编辑时必填")
    private Long promptId;

    /**
     * 采集文字内容
     */
    @NotBlank(message = "采集文字内容不能为空")
    @Schema(description = "采集文字内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /**
     * 分类标签
     */
    @NotBlank(message = "分类标签不能为空")
    @Schema(description = "分类标签", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    /**
     * 状态 0正常 1禁用
     */
    @Schema(description = "状态 0正常 1禁用")
    private String status;

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
