package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDeviceBind;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 设备绑定业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "设备绑定业务对象")
@AutoMapper(target = ChDeviceBind.class, reverseConvertGenerate = false)
public class ChDeviceBindBo extends BaseEntity {

    @Schema(description = "绑定ID")
    private Long bindId;

    /**
     * 患者端自助绑定时由控制器从登录态强制注入（PatientContextHelper），前端传值会被覆盖；
     * admin / openapi 绑定必须显式传入，为空时由 Service 层校验拦截。
     */
    @Schema(description = "患者ID（患者端由 Token 注入，无需前端传）")
    private Long patientId;

    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Schema(description = "设备类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;

    // ==================== 以下为查询条件字段（不参与绑定写入） ====================

    @Schema(description = "查询：在线状态 ONLINE/OFFLINE")
    private String onlineStatus;
}
