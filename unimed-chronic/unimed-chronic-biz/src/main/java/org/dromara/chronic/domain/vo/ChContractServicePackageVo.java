package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChContractServicePackage;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 签约服务包视图对象
 *
 * @author unimed
 */
@Schema(description = "签约服务包视图对象")
@Data
@AutoMapper(target = ChContractServicePackage.class)
public class ChContractServicePackageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "服务包ID")
    private Long packageId;
    @Schema(description = "服务包名称")
    private String packageName;
    @Schema(description = "服务包类型")
    private String packageType;
    @Schema(description = "服务项目")
    private String serviceItems;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "是否启用")
    private Boolean isActive;
}
