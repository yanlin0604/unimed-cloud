package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 转诊记录对象 ch_referral_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_referral_record")
public class ChReferralRecord extends TenantEntity {

    @TableId(value = "referral_id")
    private Long referralId;

    private Long patientId;

    private Long fromOrgId;

    private Long toOrgId;

    private String toAreaCode;

    private String referralReason;

    private String referralCategory;

    /**
     * 转诊状态: PENDING/APPROVED/ACCEPTED/REJECTED/COMPLETED
     */
    private String referralStatus;

    /**
     * 转诊类型: UPWARD/DOWNWARD/TOWNSHIP
     */
    private String referralType;

    /**
     * 转诊时间
     */
    private java.util.Date referralTime;

    @TableLogic
    private String delFlag;
}
