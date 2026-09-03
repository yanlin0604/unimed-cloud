package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.time.LocalDateTime;

/**
 * 医生端入驻执业资质审核对象 ch_doctor_qualification
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_doctor_qualification")
public class ChDoctorQualification extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long doctorUserId;

    private String doctorName;

    private String idCard;

    private String orgName;

    private String deptName;

    private String title;

    private String certificateNo;

    private String certificateImages;

    private String auditStatus;

    private String auditOpinion;

    private LocalDateTime auditTime;

    private String delFlag;
}
