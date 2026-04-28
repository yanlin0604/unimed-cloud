package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 管理路径进度表 ch_clinical_pathway_status
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_clinical_pathway_status")
public class ChClinicalPathwayStatus extends BaseEntity {

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
     * 当前所处阶段 (如: SCREENING, FIRST_EVAL, PLAN_EXECUTING, RE_EVAL)
     */
    private String currentStage;

    /**
     * 进入当前阶段时间
     */
    private Date stageStartTime;

    /**
     * 阶段截止/逾期时间
     */
    private Date stageDeadline;

    /**
     * 里程碑达成记录(JSON结构)
     */
    private String milestoneJson;
}
