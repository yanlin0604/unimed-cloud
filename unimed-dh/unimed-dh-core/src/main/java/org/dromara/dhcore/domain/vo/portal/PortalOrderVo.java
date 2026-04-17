package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端订单列表项视图对象
 *
 * @author unimed
 */
@Data
public class PortalOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 订单状态（PENDING_PAYMENT/PENDING_ASSIGN/IN_PRODUCTION/QC_REVIEW/COMPLETED/CANCELLED/REJECTED）
     */
    private String status;

    /**
     * 用户端简化状态（PENDING/PROCESSING/COMPLETED/CANCELLED/REJECTED）
     */
    private String userStatus;

    /**
     * 是否返工订单（0否 1是）
     */
    private Integer isRedo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 预计交付时间描述
     */
    private String expectedDelivery;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 封面图URL
     */
    private String coverUrl;
}
