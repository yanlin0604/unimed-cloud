package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 患者意见反馈对象 ch_patient_feedback
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_feedback")
public class ChPatientFeedback extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long patientId;

    private String feedbackType;

    private String content;

    private String contactPhone;

    private String images;

    private String replyStatus;

    private String replyContent;

    private String delFlag;
}
