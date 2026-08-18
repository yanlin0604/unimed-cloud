package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端设备绑定
 * <p>
 * 患者身份一律由 {@link PatientContextHelper#getCurrentPatientId()} 解析，
 * 绝不信任前端传入的 patientId。
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端设备")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/patient/device")
public class PatientDeviceController extends BaseController {

    private final IChDeviceBindService deviceBindService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "我的设备列表")
    @GetMapping("/list")
    public R<List<ChDeviceBindVo>> list() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(deviceBindService.queryByPatientId(patientId));
    }

    @Operation(summary = "患者自助绑定设备")
    @Log(title = "患者端设备绑定", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/bind")
    public R<Long> bind(@Validated @RequestBody ChDeviceBindBo bo) {
        // 强制覆盖前端传值，防止越权绑定到他人档案
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        bo.setBindId(null);
        return R.ok(deviceBindService.bindDevice(bo));
    }

    @Operation(summary = "患者自助解绑设备")
    @Log(title = "患者端设备解绑", businessType = BusinessType.DELETE)
    @RepeatSubmit
    @DeleteMapping("/{bindId}")
    public R<Void> unbind(@Parameter(description = "绑定ID") @PathVariable Long bindId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(deviceBindService.unbindByPatient(bindId, patientId));
    }
}
