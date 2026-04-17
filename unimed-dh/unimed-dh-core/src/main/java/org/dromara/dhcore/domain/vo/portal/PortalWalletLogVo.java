package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端钱包流水视图对象（精简版，仅暴露用户可见字段）
 *
 * @author unimed
 */
@Data
public class PortalWalletLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流水ID（Long序列化为String，防JS大数精度丢失）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 流水类型：TOPUP/CONSUME/REFUND/ADJUST
     */
    private String type;

    /**
     * 变动金额（正数=收入，负数=支出）
     */
    private BigDecimal amount;

    /**
     * 变动后余额
     */
    private BigDecimal balanceAfter;

    /**
     * 关联订单ID（Long序列化为String，可为null）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relatedOrderId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
}
