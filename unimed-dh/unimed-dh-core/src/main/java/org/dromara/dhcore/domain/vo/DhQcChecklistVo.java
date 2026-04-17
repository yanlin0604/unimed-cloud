package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 订单质检清单视图对象
 */
@Data
public class DhQcChecklistVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 口型同步是否通过
     */
    private Boolean lipSync;

    /**
     * 画面无瑕疵是否通过
     */
    private Boolean noVisualDefect;

    /**
     * 文案匹配是否通过
     */
    private Boolean scriptMatched;

    /**
     * 时长达标是否通过
     */
    private Boolean durationOk;
}
