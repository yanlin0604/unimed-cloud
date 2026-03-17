package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户处罚对象
 */
@Data
public class DhPunishBo {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 关联举报ID
     */
    private Long reportId;

    /**
     * 处罚类型（WARNING/RESTRICT/BAN�?     */
    @NotBlank(message = "处罚类型不能为空")
    private String punishType;

    /**
     * 限制天数
     */
    private Integer restrictDays;

    /**
     * 处罚原因
     */
    @NotBlank(message = "处罚原因不能为空")
    private String reason;
}
