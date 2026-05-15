package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChPatientTagDict;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者标签字典视图对象 ch_patient_tag_dict
 *
 * @author unimed
 */
@Schema(description = "患者标签字典视图对象")
@Data
@AutoMapper(target = ChPatientTagDict.class)
public class ChPatientTagDictVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "标签大类")
    private String tagType;

    @Schema(description = "标签大类名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "tagType", other = ChronicDictTypeConstant.CHRONIC_TAG_TYPE)
    private String tagTypeName;

    @Schema(description = "细分类")
    private String category;

    @Schema(description = "展示色")
    private String color;

    @Schema(description = "状态 0启用 1停用")
    private String status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "使用次数（关联 ch_patient_tag 聚合）")
    private Long useCount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "创建者")
    private Long createBy;

    @Schema(description = "更新者")
    private Long updateBy;
}
