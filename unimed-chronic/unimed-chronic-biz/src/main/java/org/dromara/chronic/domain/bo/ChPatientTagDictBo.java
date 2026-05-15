package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientTagDict;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 患者标签字典业务对象 ch_patient_tag_dict
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者标签字典业务对象")
@AutoMapper(target = ChPatientTagDict.class, reverseConvertGenerate = false)
public class ChPatientTagDictBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标签编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签编码不能为空")
    private String tagCode;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签名称不能为空")
    private String tagName;

    @Schema(description = "标签大类（RISK/CUSTOM/COMORBIDITY）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签大类不能为空")
    private String tagType;

    @Schema(description = "细分类")
    private String category;

    @Schema(description = "展示色")
    private String color;

    @Schema(description = "状态 0启用 1停用")
    private String status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "搜索关键字（名称/编码模糊）")
    private String keyword;
}
