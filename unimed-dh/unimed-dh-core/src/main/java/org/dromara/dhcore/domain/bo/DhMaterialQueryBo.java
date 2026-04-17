package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统素材查询 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "系统素材查询参数")
public class DhMaterialQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 素材名称（模糊搜索）
     */
    @Schema(description = "素材名称，支持模糊搜索")
    private String name;

    /**
     * 素材类型 IMAGE/VIDEO/AUDIO
     */
    @Schema(description = "素材类型 IMAGE/VIDEO/AUDIO")
    private String materialType;

    /**
     * 状态（0正常 1禁用）
     */
    @Schema(description = "状态 0正常 1禁用")
    private String status;
}
