package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChDoctorCustomGroup;
import org.dromara.chronic.service.ICustomGroupService;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端 - 自定义管理分组
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端自定义分组")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/custom-group")
public class DoctorCustomGroupController {

    private final ICustomGroupService customGroupService;

    /**
     * 我的分组列表
     */
    @Operation(summary = "我的分组列表")
    @SaCheckPermission("chronic:doctor:group:list")
    @GetMapping("/list")
    public R<List<ChDoctorCustomGroup>> list() {
        Long doctorId = LoginHelper.getUserId();
        return R.ok(customGroupService.listByDoctorId(doctorId));
    }

    /**
     * 创建自定义分组
     */
    @Operation(summary = "创建自定义分组")
    @SaCheckPermission("chronic:doctor:group:add")
    @Log(title = "创建自定义分组", businessType = BusinessType.INSERT)
    @PostMapping
    public R<ChDoctorCustomGroup> createGroup(
            @Parameter(description = "分组名称", required = true) @RequestParam String groupName,
            @Parameter(description = "分组描述") @RequestParam(required = false) String description) {
        Long doctorId = LoginHelper.getUserId();
        return R.ok(customGroupService.createGroup(groupName, description, doctorId));
    }

    /**
     * 更新自定义分组
     */
    @Operation(summary = "更新自定义分组")
    @SaCheckPermission("chronic:doctor:group:edit")
    @Log(title = "更新自定义分组", businessType = BusinessType.UPDATE)
    @PutMapping("/{groupId}")
    public R<Void> updateGroup(
            @Parameter(description = "分组ID", required = true) @PathVariable Long groupId,
            @Parameter(description = "分组名称", required = true) @RequestParam String groupName,
            @Parameter(description = "分组描述") @RequestParam(required = false) String description) {
        Long doctorId = LoginHelper.getUserId();
        customGroupService.updateGroup(groupId, groupName, description, doctorId);
        return R.ok();
    }

    /**
     * 删除自定义分组
     */
    @Operation(summary = "删除自定义分组")
    @SaCheckPermission("chronic:doctor:group:remove")
    @Log(title = "删除自定义分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{groupId}")
    public R<Void> deleteGroup(
            @Parameter(description = "分组ID", required = true) @PathVariable Long groupId) {
        Long doctorId = LoginHelper.getUserId();
        customGroupService.deleteGroup(groupId, doctorId);
        return R.ok();
    }

    /**
     * 批量向分组添加患者
     */
    @Operation(summary = "向分组添加患者")
    @SaCheckPermission("chronic:doctor:group:edit")
    @Log(title = "向分组添加患者", businessType = BusinessType.INSERT)
    @PostMapping("/{groupId}/patients")
    public R<Void> addPatients(
            @Parameter(description = "分组ID", required = true) @PathVariable Long groupId,
            @RequestBody List<Long> patientIds) {
        Long doctorId = LoginHelper.getUserId();
        customGroupService.addPatientsToGroup(groupId, patientIds, doctorId);
        return R.ok();
    }

    /**
     * 批量从分组移除患者
     */
    @Operation(summary = "从分组移除患者")
    @SaCheckPermission("chronic:doctor:group:edit")
    @Log(title = "从分组移除患者", businessType = BusinessType.DELETE)
    @DeleteMapping("/{groupId}/patients")
    public R<Void> removePatients(
            @Parameter(description = "分组ID", required = true) @PathVariable Long groupId,
            @RequestBody List<Long> patientIds) {
        Long doctorId = LoginHelper.getUserId();
        customGroupService.removePatientsFromGroup(groupId, patientIds, doctorId);
        return R.ok();
    }
}
