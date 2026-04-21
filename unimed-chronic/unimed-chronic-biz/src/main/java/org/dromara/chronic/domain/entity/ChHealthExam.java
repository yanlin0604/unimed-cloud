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
 * 体检报告对象 ch_health_exam
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_health_exam")
public class ChHealthExam extends TenantEntity {

    @TableId(value = "exam_id")
    private Long examId;

    private Long patientId;

    /**
     * 外部序列号（LIS 幂等）
     */
    private String externalSn;

    /**
     * 体检类型: ANNUAL_CHECKUP/REGULAR_TEST/SPECIAL_TEST
     */
    private String examType;

    private Date examDate;

    private Long examOrgId;

    /**
     * 专项类别: FUNDUS_PHOTO/ABI/NERVE_CONDUCTION/ECG/ECHO/CT
     */
    private String specialCategory;

    @TableLogic
    private String delFlag;
}
