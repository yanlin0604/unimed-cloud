package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数字人口播订单对象 dh_order
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_order")
public class DhOrder extends TenantEntity {

    /**
     * 订单ID
     */
    @TableId(value = "order_id")
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
     * 是否返工单（0否 1是）
     */
    private Integer isRedo;

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
     * 脚本文案
     */
    private String scriptText;

    /**
     * 素材摘要
     */
    private String materialSummary;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 语气风格
     */
    private String toneStyle;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 语速
     */
    private String speechSpeed;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 是否版权声明（0否 1是）
     */
    private Integer copyrightDeclared;

    /**
     * 返工原因
     */
    private String redoReason;

    /**
     * 原订单ID
     */
    private Long originalOrderId;

    /**
     * 返工次数
     */
    private Integer redoCount;

    /**
     * 成片视频地址
     */
    private String resultVideoUrl;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 驳回类型
     */
    private String rejectType;

    /**
     * 接单时间
     */
    private Date claimTime;

    /**
     * 完成时间
     */
    private Date completedTime;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
