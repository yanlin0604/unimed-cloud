package org.dromara.chronic.controller.doctor;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.manager.WarningManager;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
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
public class DoctorWarningController extends BaseController {

    private final WarningManager warningManager;
    private final IChWarningEventService warningEventService;
    private final DoctorScopeGuard doctorScopeGuard;

    /**
     * 解析预警事件归属患者并校验
     * <p>
     * detail/handle 的路径参数是 warningId，原实现零校验。handle 传入的
     * operatorId 只是记录处理人，不构成鉴权 —— 任意医生可处置他人患者的预警。
     */
    private void assertWarningOwned(Long warningId) {
        ChWarningEventVo event = warningManager.queryDetail(warningId);
        doctorScopeGuard.assertRecordOwned(event == null ? null : event.getPatientId());
    }

    @Operation(summary = "查询当前医生待办预警")
    @GetMapping("/chronic/doctor/warning/todo")
    public R<List<ChWarningEventVo>> todo() {
        // 处理人身份取自登录上下文，禁止前端传入
        return R.ok(warningEventService.queryTodoByAssignee(LoginHelper.getUserId()));
    }

    @Operation(summary = "分页查询当前医生预警")
    @GetMapping("/chronic/doctor/warning/page")
    public TableDataInfo<ChWarningEventVo> page(org.dromara.chronic.domain.bo.ChWarningEventBo bo, org.dromara.common.mybatis.core.page.PageQuery pageQuery) {
        bo.setAssigneeUserId(LoginHelper.getUserId());
        return warningEventService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "查询患者预警列表")
    @GetMapping("/chronic/doctor/warning/patient/{patientId}")
    public R<List<ChWarningEventVo>> patientWarnings(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId) {
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(warningEventService.queryByPatientId(patientId));
    }

    @Operation(summary = "预警事件详情")
    @GetMapping("/chronic/doctor/warning/{warningId}")
    public R<ChWarningEventVo> detail(@Parameter(description = "预警ID", required = true) @PathVariable Long warningId) {
        assertWarningOwned(warningId);
        return R.ok(warningManager.queryDetail(warningId));
    }

    @Operation(summary = "处理预警事件")
    @PutMapping("/chronic/doctor/warning/{warningId}/action")
    public R<Void> handle(@Parameter(description = "预警ID", required = true) @PathVariable Long warningId,
                          @Parameter(description = "操作类型", required = true) @RequestParam String actionType,
                          @Parameter(description = "操作详情") @RequestParam(required = false) String actionDetail) {
        assertWarningOwned(warningId);
        return R.ok(warningManager.handleEvent(warningId, actionType, actionDetail, LoginHelper.getUserId()));
    }
}
