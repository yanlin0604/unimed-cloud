package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.time.LocalDate;
import java.util.Date;

/**
 * 患者用药打卡明细 ch_medication_checkin
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medication_checkin")
public class ChMedicationCheckin extends TenantEntity {

    @TableId(value = "checkin_id", type = IdType.ASSIGN_ID)
    private Long checkinId;

    private Long patientId;

    private Long medId;

    private LocalDate checkinDate;

    private Date firstCheckinTime;

    private Date lastCheckinTime;
}
