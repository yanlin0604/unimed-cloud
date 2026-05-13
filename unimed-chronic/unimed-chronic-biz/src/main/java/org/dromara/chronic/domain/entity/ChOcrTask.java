package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 医疗文档OCR任务对象 ch_ocr_task
 * <p>
 * 表名重定向：原 ch_medical_document_ocr_task → 设计书规范 ch_ocr_task
 * 字段名重映射：保留原 Java 字段名以兼容现有业务代码，通过 @TableField 映射到新表列名
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ocr_task")
public class ChOcrTask extends TenantEntity {

    @TableId(value = "task_id")
    private Long taskId;

    private Long patientId;

    /** 发起端 - 旧字段名 source_type，新表为 source_terminal */
    @TableField("source_terminal")
    private String sourceType;

    /** 文档类型 - 旧字段名 document_type，新表为 doc_type */
    @TableField("doc_type")
    private String documentType;

    private String inputType;

    private Long ossId;

    private String fileUrl;

    private String fileMd5;

    /** 任务状态 - 旧字段名 status，新表为 task_status */
    @TableField("task_status")
    private String status;

    private String reportDraftJson;

    private String rawOcrJson;

    private String errorCode;

    private String errorMsg;

    private Long confirmedPatientId;

    private Integer confirmedMetricCount;

    private Long confirmedExamId;

    /** 确认人 - 旧字段名 confirmed_by，新表为 confirmer_user_id */
    @TableField("confirmer_user_id")
    private Long confirmedBy;

    /** 确认时间 - 旧字段名 confirmed_time，新表为 confirmed_at */
    @TableField("confirmed_at")
    private Date confirmedTime;

    @TableLogic
    private String delFlag;
}
