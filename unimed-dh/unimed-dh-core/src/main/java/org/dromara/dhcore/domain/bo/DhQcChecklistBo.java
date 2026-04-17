package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 质检清单请求
 */
@Data
public class DhQcChecklistBo {

    /**
     * 口型同步是否通过
     */
    @NotNull(message = "lipSync不能为空")
    private Boolean lipSync;

    /**
     * 画面无瑕疵是否通过
     */
    @NotNull(message = "noVisualDefect不能为空")
    private Boolean noVisualDefect;

    /**
     * 文案匹配是否通过
     */
    @NotNull(message = "scriptMatched不能为空")
    private Boolean scriptMatched;

    /**
     * 时长达标是否通过
     */
    @NotNull(message = "durationOk不能为空")
    private Boolean durationOk;
}
