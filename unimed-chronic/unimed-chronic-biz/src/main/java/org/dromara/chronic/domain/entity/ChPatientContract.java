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
 * 患者签约对象 ch_patient_contract
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_contract")
public class ChPatientContract extends TenantEntity {

    @TableId(value = "contract_id")
    private Long contractId;

    private Long patientId;

    private Long teamId;

    private Long packageId;

    private String contractType;

    private Date contractPeriodStart;

    private Date contractPeriodEnd;

    private String renewalStatus;

    private Boolean expiryRemindStatus;

    private String contractStatus;

    @TableLogic
    private String delFlag;
}
