package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端预警查询。
 * <p>
 * 患者ID始终从登录账号上下文解析，不接受前端传入，避免通过枚举患者ID读取他人预警。
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端预警")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientWarningController {

    private final IChWarningEventService warningEventService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询当前患者预警列表")
    @GetMapping("/chronic/patient/warning/list")
    public R<List<ChWarningEventVo>> list() {
        return R.ok(warningEventService.queryByPatientId(patientContextHelper.getCurrentPatientId()));
    }

    @Operation(summary = "查询当前患者预警详情")
    @GetMapping("/chronic/patient/warning/{warningId}")
    public R<ChWarningEventVo> detail(
        @Parameter(description = "预警事件ID", required = true) @PathVariable Long warningId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChWarningEventVo event = warningEventService.queryById(warningId);
        if (event == null || !patientId.equals(event.getPatientId())) {
            return R.fail("预警事件不存在");
        }
        return R.ok(event);
    }
}
