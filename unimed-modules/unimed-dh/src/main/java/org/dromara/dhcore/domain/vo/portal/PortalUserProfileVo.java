package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端用户个人资料视图对象
 *
 * @author unimed
 */
@Data
public class PortalUserProfileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 会员等级（NORMAL/VIP/SVIP）
     */
    private String memberLevel;

    /**
     * 会员到期时间
     */
    private Date memberExpireTime;

    /**
     * 钱包余额
     */
    private BigDecimal walletBalance;

    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 状态
     */
    private String status;
}
