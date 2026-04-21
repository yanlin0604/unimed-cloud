package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChMessageSession;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息会话业务对象")
@AutoMapper(target = ChMessageSession.class, reverseConvertGenerate = false)
public class ChMessageSessionBo extends BaseEntity {

    @Schema(description = "会话ID")
    private Long sessionId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "医生用户ID")
    private Long doctorUserId;

    @Schema(description = "会话类型")
    private String sessionType;

}
