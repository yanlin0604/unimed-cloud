package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * AI智能随访外呼任务对象 ch_ai_call_task
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ai_call_task")
public class ChAiCallTask extends TenantEntity {

    @TableId(value = "task_id")
    private Long taskId;

    private Long planId;

    private Long patientId;

    private String patientPhone;

    private String diseaseCode;

    private Integer callPriority;

    private String callStatus;

    private String audioRecordUrl;

    private String transcriptText;

    private String extractedMetrics;

    private String patientFeedback;

    private String delFlag;
}
