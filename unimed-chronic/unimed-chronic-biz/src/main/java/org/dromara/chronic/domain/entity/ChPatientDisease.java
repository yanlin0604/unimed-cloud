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
 * 患者病种对象 ch_patient_disease
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_disease")
public class ChPatientDisease extends TenantEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 病种编码
     */
    private String diseaseCode;

    /**
     * ICD 编码
     */
    private String icdCode;

    /**
     * 确诊依据
     */
    private String diagnosisBasis;

    /**
     * 确诊日期
     */
    private Date confirmDate;

    /**
     * 是否并发症
     */
    private Boolean isComplication;

    /**
     * 主病编码
     */
    private String parentDiseaseCode;

    /**
     * 管理级别(字典 chronic_manage_level)
     */
    private String manageLevel;

    /**
     * 启用状态(1启用 0停用)
     */
    private Boolean enableStatus;

    /**
     * 确诊医生用户ID
     */
    private Long diagnosisDoctorUserId;

    /**
     * 确诊机构ID
     */
    private Long diagnosisOrgId;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
