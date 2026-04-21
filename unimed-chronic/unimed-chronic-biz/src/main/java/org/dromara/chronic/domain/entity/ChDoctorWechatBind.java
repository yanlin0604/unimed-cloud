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
 * 医生微信绑定对象 ch_doctor_wechat_bind
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_doctor_wechat_bind")
public class ChDoctorWechatBind extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long userId;

    private String openid;

    private String unionid;

    private Date bindTime;

    @TableLogic
    private String delFlag;
}
