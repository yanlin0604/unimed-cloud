package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 慢病随访排期规则配置对象 ch_followup_rule
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_rule")
public class ChFollowupRule extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    /**
     * 病种编码(HTN/T2DM/COPD/CHD/STROKE/CKD/TUMOR/DYSLIPID 等，取值域为 ch_disease_config)
     */
    private String diseaseCode;

    /**
     * 风险/管理等级: LOW/MEDIUM/HIGH/VERY_HIGH/ANY(通配)
     */
    private String riskLevel;

    /**
     * 随访周期(天)
     */
    private Integer cycleDays;

    /**
     * 总轮次(一年内)
     */
    private Integer totalRounds;

    /**
     * 首轮到期天数(新建档/确诊后),默认 7
     */
    private Integer firstDueDays;

    /**
     * 默认随访方式: PHONE/ONLINE/OFFLINE/VIDEO
     */
    private String defaultVisitType;

    /**
     * 方案建议文案(展示用,原代码注释)
     */
    private String summaryAdvice;

    /**
     * 是否启用
     */
    private Boolean isActive;

    @TableLogic
    private String delFlag;
}