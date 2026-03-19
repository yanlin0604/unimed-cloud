package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包流水视图对象
 */
@Data
public class DhWalletLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 流水类型
     */
    private String type;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    private BigDecimal balanceAfter;

    /**
     * 关联订单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relatedOrderId;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
