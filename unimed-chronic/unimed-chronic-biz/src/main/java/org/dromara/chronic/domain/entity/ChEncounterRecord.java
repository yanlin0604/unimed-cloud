package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.time.LocalDateTime;

/**
 * 诊疗记录对象 ch_encounter_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_encounter_record")
public class ChEncounterRecord extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long patientId;

    private String diseaseCode;

    private String encounterType;

    private LocalDateTime encounterTime;

    private String complaint;

    private String presentHistory;

    private String physicalExamSummary;

    private String auxiliaryExamSummary;

    private String treatmentPlan;

    private String revisitAdvice;

    private String medicationSnapshot;

    private String riskFactorSnapshot;

    private String sourceType;

    private String sourceBizNo;

    private String submitStatus;

    private LocalDateTime submittedTime;

    @TableLogic
    private String delFlag;
}