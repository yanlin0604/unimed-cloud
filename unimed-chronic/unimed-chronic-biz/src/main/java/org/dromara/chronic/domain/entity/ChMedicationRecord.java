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
 * 用药记录对象 ch_medication_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medication_record")
public class ChMedicationRecord extends TenantEntity {

    @TableId(value = "med_id")
    private Long medId;

    private Long patientId;

    private String drugName;

    private String drugCode;

    private String dosage;

    private String frequency;

    private String route;

    private Date startDate;

    private Date stopDate;

    private String dispenseQuantity;

    private String prescriptionPeriod;

    private Long prescriberUserId;

    private Boolean prescriberVerified;

    private String status;

    /**
     * 用药依从性 GOOD/FAIR/POOR（字典 chronic_compliance_level）
     */
    private String compliance;

    /**
     * 处方依据
     */
    private String prescriptionBasis;

    /**
     * 用药备注
     */
    private String remark;

    @TableLogic
    private String delFlag;
}
