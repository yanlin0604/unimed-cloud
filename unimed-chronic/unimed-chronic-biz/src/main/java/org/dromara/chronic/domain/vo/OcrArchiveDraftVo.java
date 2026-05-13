package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChOcrArchiveDraft;
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
@AutoMapper(target = ChOcrArchiveDraft.class)
public class OcrArchiveDraftVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long taskId;
    private Long matchedPatientId;
    private String actionType;
    @Schema(description = "建议动作名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "actionType", other = ChronicDictTypeConstant.CHRONIC_OCR_ACTION_TYPE)
    private String actionTypeName;
    @Schema(description = "持久化的合并JSON: {profile, disease, raw}；旧三字段由 ServiceImpl 解包填充")
    private String draftData;
    private String profileDraftJson;
    private String diseaseDraftJson;
    private String unmappedFieldJson;
    private Boolean needConfirm;
    private Long confirmedPatientId;
    private String rawItemJson;
}
