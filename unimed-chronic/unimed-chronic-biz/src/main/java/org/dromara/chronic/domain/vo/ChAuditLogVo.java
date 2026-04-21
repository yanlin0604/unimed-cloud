package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChAuditLog;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 审计日志视图对象
 *
 * @author unimed
 */
@Schema(description = "审计日志视图对象")
@Data
@AutoMapper(target = ChAuditLog.class)
public class ChAuditLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "操作类型")
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

    @Schema(description = "操作时间")
    private Date operationTime;

    @Schema(description = "创建时间")
    private Date createTime;
}
