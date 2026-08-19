package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 患者独立账号对象 ch_patient_account
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_account")
public class ChPatientAccount extends TenantEntity {

    @TableId(value = "account_id")
    private Long accountId;

    private Long patientId;

    private String phone;

    private String openid;

    private String unionid;

    /**
     * 是否家属代管
     */
    private Boolean isFamilyProxy;

    /**
     * 主账号ID（家属代管时指向患者本人账号）
     */
    private Long masterAccountId;

    /**
     * 授权范围 JSON
     */
    private String authScope;

    /**
     * 授权过期时间
     */
    private Date authExpireTime;

    /**
     * 微信昵称
     */
    private String nickname;

    /**
     * 头像OSS ID
     */
    private String avatarOssId;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 绑定二维码Token
     */
    private String bindQrToken;

    /**
     * 二维码Token过期时间
     */
    private Date qrTokenExpireTime;

    @TableLogic
    private String delFlag;
}
