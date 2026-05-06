package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 医疗文档OCR报告项目草稿对象 ch_medical_document_ocr_report_item
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medical_document_ocr_report_item")
public class ChMedicalDocumentOcrReportItem extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long taskId;

    private String itemName;

    private String itemCode;

    private String resultValue;

    private String unit;

    private String referenceRange;

    private Boolean isAbnormal;

    private BigDecimal confidence;

    private Boolean needConfirm;

    private Long confirmedExamItemId;

    private String rawItemJson;

    @TableLogic
    private String delFlag;
}
