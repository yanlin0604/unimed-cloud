package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单列表项视图对象
 */
@Data
public class DhOrderItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 是否返工单
     */
    private Boolean isRedo;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 当前处理人
     */
    private String assigneeName;

    /**
     * 期望交付时长（小时）
     */
    private Integer expectDeliveryHours;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
