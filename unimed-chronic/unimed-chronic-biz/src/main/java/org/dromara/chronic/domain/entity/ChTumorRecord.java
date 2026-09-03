package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 肿瘤专病专项档案对象 ch_tumor_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_tumor_record")
public class ChTumorRecord extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long patientId;

    private String cancerType;

    private String tnmStage;

    private String pathologyResult;

    private LocalDate surgeryDate;

    private String chemoStatus;

    private BigDecimal ceaValue;

    private BigDecimal afpValue;

    private String highRiskFactors;

    private LocalDate nextReviewDate;

    private String delFlag;
}
