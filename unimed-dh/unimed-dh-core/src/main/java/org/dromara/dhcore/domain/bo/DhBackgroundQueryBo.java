package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统背景查询 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "系统背景查询参数")
public class DhBackgroundQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 背景名称（模糊搜索）
     */
    @Schema(description = "背景名称，支持模糊搜索")
    private String name;

    /**
     * 背景类型 IMAGE/VIDEO
     */
    @Schema(description = "背景类型 IMAGE/VIDEO")
    private String bgType;

    /**
     * 状态（0正常 1禁用）
     */
    @Schema(description = "状态 0正常 1禁用")
    private String status;
}
