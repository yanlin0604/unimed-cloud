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
 * 知情同意记录对象 ch_consent_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_consent_record")
public class ChConsentRecord extends TenantEntity {

    @TableId(value = "consent_id")
    private Long consentId;

    private Long patientId;

    /**
     * 同意类型: SIGN_CONTRACT/DATA_SHARE/REFERRAL
     */
    private String consentType;

    private Long signImageFileId;

    private Date signTime;

    /** 签署方式: ELECTRONIC/PAPER */
    private String signMethod;

    /** 操作人IP */
    private String operatorIp;

    /** 设备信息 */
    private String deviceInfo;

    /** 关联业务类型(CONTRACT/REFERRAL/SHARE) */
    private String relatedBizType;

    /** 关联业务ID */
    private Long relatedBizId;

    @TableLogic
    private String delFlag;
}
