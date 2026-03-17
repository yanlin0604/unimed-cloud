package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单处理日志视图对象
 */
@Data
public class DhProcessLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 操作时间
     */
    private Date time;

    /**
     * 操作动作
     */
    private String action;

    /**
     * 操作�?     */
    private String operator;
}
