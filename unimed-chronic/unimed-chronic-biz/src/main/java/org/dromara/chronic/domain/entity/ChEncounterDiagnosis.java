package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 诊疗诊断对象 ch_encounter_diagnosis
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_encounter_diagnosis")
public class ChEncounterDiagnosis extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long encounterId;

    private Long patientId;

    private String diagnosisType;

    private String diagnosisCode;

    private String diagnosisName;

    private String diagnosisBasis;

    private String riskFactorCode;

    private String riskFactorName;

    private String complicationFlag;

    @TableLogic
    private String delFlag;
}