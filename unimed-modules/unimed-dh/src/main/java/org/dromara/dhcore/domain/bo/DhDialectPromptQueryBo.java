package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言采集提示文字查询 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "提示文字查询参数")
public class DhDialectPromptQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文字内容（模糊搜索）
     */
    @Schema(description = "文字内容，支持模糊搜索")
    private String content;

    /**
     * 分类标签
     */
    @Schema(description = "分类标签")
    private String category;

    /**
     * 状态（0正常 1禁用）
     */
    @Schema(description = "状态 0正常 1禁用")
    private String status;
}
