package org.dromara.chronic.controller.doctor;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.manager.TeamManager;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医生端团队管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端团队")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/team")
public class DoctorTeamController extends BaseController {

    private final TeamManager teamManager;
    private final DoctorScopeGuard doctorScopeGuard;

    @Operation(summary = "解散团队")
    @SaCheckPermission("chronic:doctor:team:dissolve")
    @Log(title = "解散团队", businessType = BusinessType.UPDATE)
    @PostMapping("/{teamId}/dissolve")
    public R<Void> dissolve(@Parameter(description = "团队ID", required = true) @PathVariable Long teamId) {
        // TeamManager.dissolveTeam 只按 id 置 DISSOLVED、无任何归属校验，
        // 而 8 个医生共用同一角色都持有 dissolve 权限码 —— 不校验则任意医生可解散任意团队。
        doctorScopeGuard.assertTeamLeader(teamId);
        return teamManager.dissolveTeam(teamId) ? R.ok() : R.fail();
    }

    @Operation(summary = "患者重分配")
    @SaCheckPermission("chronic:doctor:team:reassign")
    @Log(title = "患者重分配", businessType = BusinessType.UPDATE)
    @PostMapping("/{teamId}/reassign-patients")
    @SuppressWarnings("unchecked")
    public R<Void> reassignPatients(@Parameter(description = "团队ID", required = true) @PathVariable Long teamId, @RequestBody Map<String, Object> body) {
        // 源团队负责人才能迁出患者
        doctorScopeGuard.assertTeamLeader(teamId);
        Long targetTeamId = body.get("targetTeamId") == null ? null : Long.valueOf(String.valueOf(body.get("targetTeamId")));
        if (targetTeamId == null) {
            return R.fail("targetTeamId不能为空");
        }
        List<Long> patientIds = body.get("patientIds") instanceof List<?> list
            ? list.stream().map(item -> Long.valueOf(String.valueOf(item))).toList()
            : List.of();
        // 关键：TeamManager.reassignPatients 在 patientIds 为空时**不拼 in 条件**，
        // 等于把该团队全部签约患者一次性改派走。必须显式要求目标患者，
        // 且逐个校验归属，防止把他人患者迁入自己团队。
        doctorScopeGuard.assertPatientsOwned(patientIds);
        return teamManager.reassignPatients(teamId, targetTeamId, patientIds) ? R.ok() : R.fail();
    }
}
