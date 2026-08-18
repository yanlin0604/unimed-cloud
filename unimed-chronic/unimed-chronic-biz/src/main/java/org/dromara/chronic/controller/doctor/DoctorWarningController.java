package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.manager.WarningManager;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端预警
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端预警")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class DoctorWarningController {

    private final WarningManager warningManager;
    private final IChWarningEventService warningEventService;

    @Operation(summary = "查询当前医生待办预警")
    @GetMapping("/chronic/doctor/warning/todo")
    public R<List<ChWarningEventVo>> todo() {
        // 处理人身份取自登录上下文，禁止前端传入
        return R.ok(warningEventService.queryTodoByAssignee(LoginHelper.getUserId()));
    }

    @Operation(summary = "查询患者预警列表")
    @GetMapping("/chronic/doctor/warning/patient/{patientId}")
    public R<List<ChWarningEventVo>> patientWarnings(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(warningEventService.queryByPatientId(patientId));
    }

    @Operation(summary = "预警事件详情")
    @GetMapping("/chronic/doctor/warning/{warningId}")
    public R<ChWarningEventVo> detail(@Parameter(description = "预警ID", required = true) @PathVariable Long warningId) {
        return R.ok(warningManager.queryDetail(warningId));
    }

    @Operation(summary = "处理预警事件")
    @PutMapping("/chronic/doctor/warning/{warningId}/action")
    public R<Void> handle(@Parameter(description = "预警ID", required = true) @PathVariable Long warningId,
                          @Parameter(description = "操作类型", required = true) @RequestParam String actionType,
                          @Parameter(description = "操作详情") @RequestParam(required = false) String actionDetail) {
        return R.ok(warningManager.handleEvent(warningId, actionType, actionDetail, LoginHelper.getUserId()));
    }
}
