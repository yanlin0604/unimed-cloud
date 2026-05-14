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
 * 检查记录对象 ch_medical_exam
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medical_exam")
public class ChMedicalExam extends TenantEntity {

    @TableId(value = "exam_id")
    private Long examId;

    private Long patientId;

    private Date examDate;

    private String examType;

    private String examPart;

    private String examResult;

    private String examConclusion;

    private String reportImage;

    private String hospital;

    private String doctor;

    private String remark;

    @TableLogic
    private String delFlag;
}
