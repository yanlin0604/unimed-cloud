package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.time.LocalDate;

/**
 * 非药物治疗与康复记录对象 ch_treatment_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_treatment_record")
public class ChTreatmentRecord extends TenantEntity {

    @TableId(value = "treatment_id")
    private Long treatmentId;

    private Long patientId;

    private Long encounterId;

    private String diseaseCode;

    private String treatmentType;

    private String treatmentName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String courseDesc;

    private String effectEvaluation;

    private String operatorDoctorName;

    private String status;

    private String remark;

    private String delFlag;
}
