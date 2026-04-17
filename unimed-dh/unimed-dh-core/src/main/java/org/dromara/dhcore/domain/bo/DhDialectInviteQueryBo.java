package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言邀请码配置查询 BO
 *
 * @author unimed
 */
@Data
@Schema(description = "邀请码配置查询参数")
public class DhDialectInviteQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 语种名
     */
    @Schema(description = "语种名")
    private String dialectName;

    /**
     * 邀请码
     */
    @Schema(description = "邀请码")
    private String inviteCode;
}
