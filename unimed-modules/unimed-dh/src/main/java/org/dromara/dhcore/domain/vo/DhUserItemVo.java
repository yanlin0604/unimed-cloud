package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户列表视图对象
 */
@Data
public class DhUserItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 钱包余额
     */
    private BigDecimal walletBalance;

    /**
     * 累计充值
     */
    private BigDecimal totalTopup;

    /**
     * 累计消费
     */
    private BigDecimal totalConsume;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
