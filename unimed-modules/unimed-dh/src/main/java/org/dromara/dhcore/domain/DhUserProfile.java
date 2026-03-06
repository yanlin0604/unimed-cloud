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
 * 数字人口播用户画像对象 dh_user_profile
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_user_profile")
public class DhUserProfile extends TenantEntity {

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

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
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
