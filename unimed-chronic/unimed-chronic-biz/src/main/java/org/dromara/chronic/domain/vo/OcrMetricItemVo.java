package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChOcrMetricItem;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 医疗文档OCR指标草稿视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "医疗文档OCR指标草稿视图对象")
@AutoMapper(target = ChOcrMetricItem.class)
public class OcrMetricItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long taskId;
    private String originalName;
    private String metricType;
    @Schema(description = "指标类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "metricType", other = ChronicDictTypeConstant.CHRONIC_METRIC_TYPE)
    private String metricTypeName;
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
}
