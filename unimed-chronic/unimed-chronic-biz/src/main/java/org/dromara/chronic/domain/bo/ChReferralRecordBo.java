package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChReferralRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 转诊记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "转诊记录业务对象")
@AutoMapper(target = ChReferralRecord.class, reverseConvertGenerate = false)
public class ChReferralRecordBo extends BaseEntity {

    @Schema(description = "转诊ID")
    private Long referralId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "来源机构ID")
    private Long fromOrgId;

    @Schema(description = "目标机构ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标机构不能为空")
    private Long toOrgId;

    @Schema(description = "目标地区编码")
    private String toAreaCode;

    @Schema(description = "转诊原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "转诊原因不能为空")
    private String referralReason;

    @Schema(description = "转诊分类")
    private String referralCategory;

    @Schema(description = "转诊类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "转诊类型不能为空")
    private String referralType;

    @Schema(description = "转诊状态")
    private String referralStatus;
}
