package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupRecordBo;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.manager.FollowupManager;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端随访控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端随访")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/patient/followup")
public class PatientFollowupController {

    private final IChFollowupService followupService;
    private final FollowupManager followupManager;

    @Operation(summary = "查询随访计划")
    @GetMapping("/plan")
    public R<ChFollowupPlanVo> plan() {
        Long patientId = LoginHelper.getUserId();
        return R.ok(followupService.queryCurrentPlan(patientId));
    }

    @Operation(summary = "查询随访任务")
    @GetMapping("/task")
    public R<List<ChFollowupTaskVo>> task() {
        Long patientId = LoginHelper.getUserId();
        return R.ok(followupService.queryPatientTasks(patientId));
    }

    @Operation(summary = "患者自填随访提交")
    @Log(title = "患者自填随访", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/task/{taskId}/submit")
    public R<Long> submit(@Parameter(description = "任务ID") @PathVariable Long taskId, @Validated @RequestBody ChFollowupRecordBo bo) {
        bo.setTaskId(taskId);
        bo.setVisitType("SELF_FILL");
        return R.ok(followupManager.completeTask(bo));
    }
}
