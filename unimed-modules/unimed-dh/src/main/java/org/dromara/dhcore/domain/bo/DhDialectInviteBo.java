package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言邀请码配置新增/编辑 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "邀请码配置新增/编辑参数")
public class DhDialectInviteBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请配置ID（编辑时必填）
     */
    @Schema(description = "邀请配置ID，编辑时必填")
    private Long inviteId;

    /**
     * 语种名（与C端方言名称一致）
     */
    @NotBlank(message = "语种名不能为空")
    @Schema(description = "语种名，如：湘语·株洲话", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dialectName;

    /**
     * 邀请码（新增时系统自动生成，编辑时只读）
     */
    @Schema(description = "邀请码，新增时自动生成")
    private String inviteCode;

    /**
     * 扩展信息（JSON格式，如邀请人等）
     */
    @Schema(description = "扩展信息（JSON格式，如邀请人等）")
    private String extInfo;

    /**
     * 状态
     */
    @Schema(description = "状态 0正常 1禁用")
    private String status;
}
