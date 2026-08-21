package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.List;

/**
 * 管理方案业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理方案业务对象")
@AutoMapper(target = ChManagePlan.class, reverseConvertGenerate = false)
public class ChManagePlanBo extends BaseEntity {

    @Schema(description = "方案ID")
    @NotNull(message = "方案ID不能为空", groups = {EditGroup.class})
    private Long planId;

    @Schema(description = "患者ID")
    @NotNull(message = "患者ID不能为空", groups = {AddGroup.class})
    private Long patientId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空", groups = {AddGroup.class, EditGroup.class})
    private String diseaseCode;

    @Schema(description = "方案状态")
    private String planStatus;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "方案名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "方案名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String planName;

    @Schema(description = "方案备注")
    private String planRemark;
    @Schema(description = "方案子项列表")
    private List<ChManagePlanItemBo> itemList;
}
