package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrReportItem;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 医疗文档OCR报告项目草稿视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "医疗文档OCR报告项目草稿视图对象")
@AutoMapper(target = ChMedicalDocumentOcrReportItem.class)
public class MedicalDocumentOcrReportItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
}
