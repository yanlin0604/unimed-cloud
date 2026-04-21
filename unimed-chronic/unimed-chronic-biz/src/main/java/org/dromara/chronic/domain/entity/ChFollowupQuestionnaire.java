package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 随访问卷模板对象 ch_followup_questionnaire
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_questionnaire")
public class ChFollowupQuestionnaire extends TenantEntity {

    @TableId(value = "questionnaire_id")
    private Long questionnaireId;

    private String diseaseCode;

    private String questionnaireName;

    private Integer version;

    /**
     * 问卷题目 JSON（含跳题逻辑 skip_logic）
     */
    private String questions;

    /**
     * 是否国家公卫标准模板
     */
    private Boolean isNationalStandard;

    /**
     * 是否启用
     */
    private Boolean isActive;

    @TableLogic
    private String delFlag;
}
