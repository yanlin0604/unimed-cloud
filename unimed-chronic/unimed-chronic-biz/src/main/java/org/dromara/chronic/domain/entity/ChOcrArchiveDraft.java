package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 医疗文档OCR建档草稿对象 ch_ocr_draft (draft_category='PROFILE')
 * <p>
 * 表名重定向：原 ch_medical_document_ocr_archive_draft → 设计书规范 ch_ocr_draft
 * 通过 draft_category='PROFILE' 与 METRIC/REPORT 类记录区分
 * <p>
 * 字段策略：
 *  - 旧表的 profile_draft_json / disease_draft_json / raw_item_json 三个 JSON 字段
 *    在新表中被合并到 draft_data 一个 JSON 字段
 *  - 为兼容业务代码（Parser/Service 仍 set 旧字段），Java 层保留 3 个旧字段为非持久化（exist=false），
 *    由 Service 在 insert 前调用 OcrDraftDataConverter.packArchive() 打包，
 *    queryById 后调用 OcrDraftDataConverter.unpackArchive() 还原
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ocr_draft")
public class ChOcrArchiveDraft extends TenantEntity {

    /** 主键 - 旧字段名 id，新表为 draft_id */
    @TableId(value = "draft_id")
    private Long id;

    private Long taskId;

    /** 草稿类型识别字段，本 Entity 固定为 PROFILE */
    @TableField("draft_category")
    private String draftCategory = "PROFILE";

    /**
     * 实际持久化的合并 JSON 列：{"profile":..., "disease":..., "raw":...}
     * Service 层在 insert 前由 OcrDraftDataConverter.packArchive() 组装
     */
    @TableField("draft_data")
    private String draftData;

    private Long matchedPatientId;

    private String actionType;

    private String unmappedFieldJson;

    private Boolean needConfirm;

    /** 确认入库后的患者ID - 旧字段名 confirmed_patient_id，新表为 written_biz_id */
    @TableField("written_biz_id")
    private Long confirmedPatientId;

    /** 业务逻辑字段，不持久化（实际拼接到 draftData 中） */
    @TableField(exist = false)
    private String profileDraftJson;

    /** 业务逻辑字段，不持久化（实际拼接到 draftData 中） */
    @TableField(exist = false)
    private String diseaseDraftJson;

    /** 业务逻辑字段，不持久化（实际拼接到 draftData 中） */
    @TableField(exist = false)
    private String rawItemJson;

    @TableLogic
    private String delFlag;
}
