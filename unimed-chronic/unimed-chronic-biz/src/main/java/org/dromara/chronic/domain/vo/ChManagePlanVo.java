package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 管理方案视图对象
 *
 * @author unimed
 */
@Schema(description = "管理方案视图对象")
@Data
@AutoMapper(target = ChManagePlan.class)
public class ChManagePlanVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "方案状态")
    private String planStatus;
    @Schema(description = "机构ID")
    private Long orgId;
    @Schema(description = "方案名称")
    private String planName;
    @Schema(description = "方案备注")
    private String planRemark;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "子项列表")
    private List<ChManagePlanItemVo> itemList;

    @Schema(description = "方案状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "planStatus", other = ChronicDictTypeConstant.CHRONIC_PLAN_STATUS)
    private String planStatusName;

    @Schema(description = "病种名称")
    private String diseaseName;
}
