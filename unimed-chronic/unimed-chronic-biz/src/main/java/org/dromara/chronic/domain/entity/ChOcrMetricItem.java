package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 医疗文档OCR指标草稿对象 ch_ocr_draft (draft_category='METRIC')
 * <p>
 * 表名重定向：原 ch_medical_document_ocr_metric_item → 设计书规范 ch_ocr_draft
 * 通过 draft_category='METRIC' 与 PROFILE/REPORT 类记录区分
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ocr_draft")
public class ChOcrMetricItem extends TenantEntity {

    /** 主键 - 旧字段名 id，新表为 draft_id */
    @TableId(value = "draft_id")
    private Long id;

    private Long taskId;

    /** 草稿类型识别字段，本 Entity 固定为 METRIC */
    @TableField("draft_category")
    private String draftCategory = "METRIC";

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

    /** 确认入库后的指标ID - 旧字段名 confirmed_metric_id，新表为 written_biz_id */
    @TableField("written_biz_id")
    private Long confirmedMetricId;

    /** 原始项目JSON - 旧字段名 raw_item_json，新表为 draft_data */
    @TableField("draft_data")
    private String rawItemJson;

    @TableLogic
    private String delFlag;
}
