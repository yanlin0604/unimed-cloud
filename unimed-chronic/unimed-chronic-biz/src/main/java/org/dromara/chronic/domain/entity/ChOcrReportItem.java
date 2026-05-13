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
 * 医疗文档OCR报告项目草稿对象 ch_ocr_draft (draft_category='REPORT')
 * <p>
 * 表名重定向：原 ch_medical_document_ocr_report_item → 设计书规范 ch_ocr_draft
 * 通过 draft_category='REPORT' 与 PROFILE/METRIC 类记录区分
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ocr_draft")
public class ChOcrReportItem extends TenantEntity {

    /** 主键 - 旧字段名 id，新表为 draft_id */
    @TableId(value = "draft_id")
    private Long id;

    private Long taskId;

    /** 草稿类型识别字段，本 Entity 固定为 REPORT */
    @TableField("draft_category")
    private String draftCategory = "REPORT";

    private String itemName;

    private String itemCode;

    private String resultValue;

    private String unit;

    private String referenceRange;

    private Boolean isAbnormal;

    private BigDecimal confidence;

    private Boolean needConfirm;

    /** 确认入库后的报告项目ID - 旧字段名 confirmed_exam_item_id，新表为 written_biz_id */
    @TableField("written_biz_id")
    private Long confirmedExamItemId;

    /** 原始项目JSON - 旧字段名 raw_item_json，新表为 draft_data */
    @TableField("draft_data")
    private String rawItemJson;

    @TableLogic
    private String delFlag;
}
