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
 * 医疗文档OCR指标草稿对象 ch_medical_document_ocr_metric_item
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medical_document_ocr_metric_item")
public class ChMedicalDocumentOcrMetricItem extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long taskId;

    private String originalName;

    private String metricType;

    private String metricValue;

    private String unit;

    private BigDecimal referenceValueMin;

    private BigDecimal referenceValueMax;

    private String referenceRange;

    private Boolean isAbnormal;

    private BigDecimal confidence;

    private Boolean needConfirm;

    private Long confirmedMetricId;

    private String rawItemJson;

    @TableLogic
    private String delFlag;
}
