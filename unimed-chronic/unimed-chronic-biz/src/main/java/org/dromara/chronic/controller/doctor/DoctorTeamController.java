package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.manager.TeamManager;
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
public class DoctorTeamController {

    private final TeamManager teamManager;

    @Operation(summary = "解散团队")
    @SaCheckPermission("chronic:doctor:team:dissolve")
    @Log(title = "解散团队", businessType = BusinessType.UPDATE)
    @PostMapping("/{teamId}/dissolve")
    public R<Void> dissolve(@Parameter(description = "团队ID", required = true) @PathVariable Long teamId) {
        return teamManager.dissolveTeam(teamId) ? R.ok() : R.fail();
    }

    @Operation(summary = "患者重分配")
    @SaCheckPermission("chronic:doctor:team:reassign")
    @Log(title = "患者重分配", businessType = BusinessType.UPDATE)
    @PostMapping("/{teamId}/reassign-patients")
    @SuppressWarnings("unchecked")
    public R<Void> reassignPatients(@Parameter(description = "团队ID", required = true) @PathVariable Long teamId, @RequestBody Map<String, Object> body) {
        Long targetTeamId = body.get("targetTeamId") == null ? null : Long.valueOf(String.valueOf(body.get("targetTeamId")));
        List<Long> patientIds = body.get("patientIds") instanceof List<?> list
            ? list.stream().map(item -> Long.valueOf(String.valueOf(item))).toList()
            : List.of();
        return teamManager.reassignPatients(teamId, targetTeamId, patientIds) ? R.ok() : R.fail();
    }
}
