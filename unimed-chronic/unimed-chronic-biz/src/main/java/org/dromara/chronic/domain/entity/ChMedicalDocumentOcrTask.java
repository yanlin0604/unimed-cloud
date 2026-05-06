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
 * 医疗文档OCR任务对象 ch_medical_document_ocr_task
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medical_document_ocr_task")
public class ChMedicalDocumentOcrTask extends TenantEntity {

    @TableId(value = "task_id")
    private Long taskId;

    private Long patientId;

    private String sourceType;

    private String documentType;

    private String inputType;

    private Long ossId;

    private String fileUrl;

    private String fileMd5;

    private String status;

    private String reportDraftJson;

    private String rawOcrJson;

    private String errorCode;

    private String errorMsg;

    private Long confirmedPatientId;

    private Integer confirmedMetricCount;

    private Long confirmedExamId;

    private Long confirmedBy;

    private Date confirmedTime;

    @TableLogic
    private String delFlag;
}
