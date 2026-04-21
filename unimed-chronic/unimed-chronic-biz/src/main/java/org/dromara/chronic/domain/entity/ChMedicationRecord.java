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

    private Integer dispenseQuantity;

    private Integer prescriptionPeriod;

    private Long prescriberUserId;

    private Boolean prescriberVerified;

    private String status;

    @TableLogic
    private String delFlag;
}
