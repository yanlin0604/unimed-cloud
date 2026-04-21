package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 患者签约业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者签约业务对象")
@AutoMapper(target = ChPatientContract.class, reverseConvertGenerate = false)
public class ChPatientContractBo extends BaseEntity {

    @Schema(description = "签约ID")
    private Long contractId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "团队ID")
    private Long teamId;

    @Schema(description = "服务包ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "服务包ID不能为空")
    private Long packageId;

    @Schema(description = "签约类型")
    private String contractType;

    @Schema(description = "签约开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "签约开始时间不能为空")
    private Date contractPeriodStart;

    @Schema(description = "签约结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "签约结束时间不能为空")
    private Date contractPeriodEnd;

    @Schema(description = "续签状态")
    private String renewalStatus;

    @Schema(description = "到期提醒状态")
    private Boolean expiryRemindStatus;

    @Schema(description = "签约状态")
    private String contractStatus;
}
