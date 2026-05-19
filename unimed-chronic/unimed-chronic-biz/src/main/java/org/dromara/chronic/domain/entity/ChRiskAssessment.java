package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 风险评估对象 ch_risk_assessment
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_risk_assessment")
public class ChRiskAssessment extends TenantEntity {

    @TableId(value = "assessment_id")
    private Long assessmentId;

    private Long patientId;

    private String diseaseCode;

    private String riskLevel;

    /**
     * 风险评估报告 JSON/文本
     */
    private String assessmentReport;

    private Long assessorUserId;

    /**
     * 机构ID（对应系统库 sys_dept.dept_id）
     */
    private Long orgId;

    @TableLogic
    private String delFlag;
}
