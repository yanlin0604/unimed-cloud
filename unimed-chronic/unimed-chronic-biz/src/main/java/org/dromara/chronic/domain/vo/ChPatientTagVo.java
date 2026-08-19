package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者标签视图对象 ch_patient_tag
 *
 * @author unimed
 */
@Schema(description = "患者标签视图对象")
@Data
@AutoMapper(target = ChPatientTag.class)
public class ChPatientTagVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "标签大类")
    private String tagType;

    @Schema(description = "标签大类名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "tagType", other = ChronicDictTypeConstant.CHRONIC_TAG_TYPE)
    private String tagTypeName;

    @Schema(description = "标签字典编码")
    private String tagCode;

    @Schema(description = "标签值/显示名称")
    private String tagValue;

    @Schema(description = "标签名称（按 tagCode 查 ch_patient_tag_dict 回填）")
    private String tagName;

    @Schema(description = "展示色（按 tagCode 查 ch_patient_tag_dict 回填）")
    private String tagColor;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "创建者")
    private Long createBy;

    @Schema(description = "更新者")
    private Long updateBy;
}
