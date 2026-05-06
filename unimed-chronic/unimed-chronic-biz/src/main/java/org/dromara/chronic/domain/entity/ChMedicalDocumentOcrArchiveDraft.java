package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 医疗文档OCR建档草稿对象 ch_medical_document_ocr_archive_draft
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_medical_document_ocr_archive_draft")
public class ChMedicalDocumentOcrArchiveDraft extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long taskId;

    private Long matchedPatientId;

    private String actionType;

    private String profileDraftJson;

    private String diseaseDraftJson;

    private String unmappedFieldJson;

    private Boolean needConfirm;

    private Long confirmedPatientId;

    private String rawItemJson;

    @TableLogic
    private String delFlag;
}
