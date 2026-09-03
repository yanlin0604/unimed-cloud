package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 门诊与慢病长处方对象 ch_prescription
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_prescription")
public class ChPrescription extends TenantEntity {

    @TableId(value = "prescription_id")
    private Long prescriptionId;

    private String prescriptionNo;

    private Long patientId;

    private String patientName;

    private Long doctorUserId;

    private String doctorName;

    private Long encounterId;

    private String diagnosis;

    private String prescriptionType;

    private String status;

    private String itemsJson;

    private BigDecimal totalAmount;

    private LocalDateTime prescriptionTime;

    private String delFlag;
}
