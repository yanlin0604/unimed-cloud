package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChContractServicePackage;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 签约服务包业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "签约服务包业务对象")
@AutoMapper(target = ChContractServicePackage.class, reverseConvertGenerate = false)
public class ChContractServicePackageBo extends BaseEntity {

    @Schema(description = "服务包ID")
    private Long packageId;

    @Schema(description = "服务包名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "服务包名称不能为空")
    private String packageName;

    @Schema(description = "服务包类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "服务包类型不能为空")
    private String packageType;

    @Schema(description = "服务项目")
    private String serviceItems;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "服务周期(月)")
    private Integer servicePeriod;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
