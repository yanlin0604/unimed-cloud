package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChDiseaseRelation;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * 病种关系视图对象
 *
 * @author unimed
 */
@Schema(description = "病种关系视图对象")
@Data
@AutoMapper(target = ChDiseaseRelation.class)
public class ChDiseaseRelationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "父级病种编码")
    private String parentDiseaseCode;
    @Schema(description = "父级病种名称")
    private String parentDiseaseName;
    @Schema(description = "并发症病种编码")
    private String complicationDiseaseCode;
    @Schema(description = "并发症病种名称")
    private String complicationDiseaseName;
    @Schema(description = "关系类型")
    private String relationType;
    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "关联类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "relationType", other = ChronicDictTypeConstant.CHRONIC_RELATION_TYPE)
    private String relationTypeName;
}
