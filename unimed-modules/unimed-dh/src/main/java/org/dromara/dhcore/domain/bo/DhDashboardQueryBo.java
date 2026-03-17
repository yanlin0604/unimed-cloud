package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 看板指标查询参数
 */
@Data
public class DhDashboardQueryBo {

    /**
     * 统计时间范围（today/7d/30d�?     */
    @NotBlank(message = "时间范围不能为空")
    private String range;
}
