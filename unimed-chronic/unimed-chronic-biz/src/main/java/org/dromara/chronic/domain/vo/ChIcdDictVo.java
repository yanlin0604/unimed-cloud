package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChIcdDict;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * ICD 字典视图对象
 *
 * @author unimed
 */
@Schema(description = "ICD字典视图对象")
@Data
@AutoMapper(target = ChIcdDict.class)
public class ChIcdDictVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "ICD编码")
    private String icdCode;
    @Schema(description = "ICD版本")
    private String icdVersion;
    @Schema(description = "ICD中文名称")
    private String icdNameCn;
    @Schema(description = "ICD英文名称")
    private String icdNameEn;
    @Schema(description = "分类")
    private String category;

    @Schema(description = "ICD版本名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "icdVersion", other = ChronicDictTypeConstant.CHRONIC_ICD_VERSION)
    private String icdVersionName;
}
