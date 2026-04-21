package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 审计日志业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "审计日志业务对象")
@AutoMapper(target = ChAuditLog.class, reverseConvertGenerate = false)
public class ChAuditLogBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "操作类型不能为空")
    private String operationType;

    @Schema(description = "操作目标")
    private String operationTarget;

    @Schema(description = "操作详情")
    private String operationDetail;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "操作人IP")
    private String operatorIp;
}
