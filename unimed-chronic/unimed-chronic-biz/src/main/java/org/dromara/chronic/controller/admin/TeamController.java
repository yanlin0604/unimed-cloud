package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDoctorTeamBo;
import org.dromara.chronic.domain.bo.ChDoctorTeamMemberBo;
import org.dromara.chronic.domain.vo.ChDoctorTeamMemberVo;
import org.dromara.chronic.domain.vo.ChDoctorTeamVo;
import org.dromara.chronic.service.IChDoctorTeamService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台团队管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生团队")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/team")
public class TeamController extends BaseController {

    private final IChDoctorTeamService doctorTeamService;

    @Operation(summary = "分页查询团队")
    @SaCheckPermission("chronic:team:list")
    @GetMapping("/page")
    public TableDataInfo<ChDoctorTeamVo> page(ChDoctorTeamBo bo, PageQuery pageQuery) {
        return doctorTeamService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "新增团队")
    @SaCheckPermission("chronic:team:add")
    @Log(title = "医生团队", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody ChDoctorTeamBo bo) {
        return toAjax(doctorTeamService.insertByBo(bo));
    }

    @Operation(summary = "修改团队")
    @SaCheckPermission("chronic:team:edit")
    @Log(title = "医生团队", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{teamId}")
    public R<Void> edit(@Parameter(description = "团队ID") @PathVariable Long teamId, @Validated @RequestBody ChDoctorTeamBo bo) {
        bo.setTeamId(teamId);
        return toAjax(doctorTeamService.updateByBo(bo));
    }

    @Operation(summary = "删除团队")
    @SaCheckPermission("chronic:team:remove")
    @Log(title = "医生团队", businessType = BusinessType.DELETE)
    @DeleteMapping("/{teamId}")
    public R<Void> remove(@Parameter(description = "团队ID") @PathVariable Long teamId) {
        return toAjax(doctorTeamService.deleteById(teamId));
    }

    @Operation(summary = "新增团队成员")
    @SaCheckPermission("chronic:team:member:add")
    @Log(title = "团队成员", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/{teamId}/member")
    public R<Void> addMember(@Parameter(description = "团队ID") @PathVariable Long teamId, @Validated @RequestBody ChDoctorTeamMemberBo bo) {
        bo.setTeamId(teamId);
        return toAjax(doctorTeamService.addMember(bo));
    }

    @Operation(summary = "移除团队成员")
    @SaCheckPermission("chronic:team:member:remove")
    @Log(title = "团队成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{teamId}/member/{userId}")
    public R<Void> removeMember(@Parameter(description = "团队ID") @PathVariable Long teamId, @Parameter(description = "用户ID") @PathVariable Long userId) {
        return toAjax(doctorTeamService.removeMember(teamId, userId));
    }

    @Operation(summary = "查询团队成员")
    @SaCheckPermission("chronic:team:query")
    @GetMapping("/{teamId}/members")
    public R<List<ChDoctorTeamMemberVo>> members(@Parameter(description = "团队ID") @PathVariable Long teamId) {
        return R.ok(doctorTeamService.queryMembers(teamId));
    }
}
