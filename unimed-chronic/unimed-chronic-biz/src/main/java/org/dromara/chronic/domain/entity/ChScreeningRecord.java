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
 * 义诊筛查记录对象 ch_screening_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_screening_record")
public class ChScreeningRecord extends TenantEntity {

    @TableId(value = "record_id")
    private Long recordId;

    private Long batchId;

    private String offlineUuid;

    private String patientName;

    private String idCard;

    private String phone;

    private String gender;

    private Integer age;

    /**
     * 症状 JSON
     */
    private String symptoms;

    /**
     * 体征/指标 JSON
     */
    private String vitals;

    private String riskLevel;

    private String conclusion;

    private String enrollStatus;

    private Long enrolledPatientId;

    private Date uploadTime;

    @TableLogic
    private String delFlag;
}
