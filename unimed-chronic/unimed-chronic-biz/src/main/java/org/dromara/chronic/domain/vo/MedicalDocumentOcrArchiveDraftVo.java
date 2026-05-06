package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrArchiveDraft;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * 医疗文档OCR建档草稿视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "医疗文档OCR建档草稿视图对象")
@AutoMapper(target = ChMedicalDocumentOcrArchiveDraft.class)
public class MedicalDocumentOcrArchiveDraftVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long taskId;
    private Long matchedPatientId;
    private String actionType;
    @Schema(description = "建议动作名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "actionType", other = ChronicDictTypeConstant.CHRONIC_OCR_ACTION_TYPE)
    private String actionTypeName;
    private String profileDraftJson;
    private String diseaseDraftJson;
    private String unmappedFieldJson;
    private Boolean needConfirm;
    private Long confirmedPatientId;
    private String rawItemJson;
}
