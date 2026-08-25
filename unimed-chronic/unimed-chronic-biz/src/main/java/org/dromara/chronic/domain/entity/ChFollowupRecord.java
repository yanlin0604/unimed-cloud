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
 * 随访记录对象 ch_followup_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_record")
public class ChFollowupRecord extends TenantEntity {

    @TableId(value = "record_id")
    private Long recordId;

    private Long taskId;

    private Long patientId;

    private String visitType;

    private String visitContent;

    private Long visitorUserId;

    private Date visitDate;

    /** 随访结论(CONTROLLED/IMPROVING/UNCONTROLLED/DETERIORATING/REFERRAL) */
    private String followupResult;

    /** 康复评级(EXCELLENT/GOOD/FAIR/POOR) */
    private String rehabLevel;

    /** 随访回报与健康指导建议 */
    private String feedbackAdvice;

    @TableLogic
    private String delFlag;
}
