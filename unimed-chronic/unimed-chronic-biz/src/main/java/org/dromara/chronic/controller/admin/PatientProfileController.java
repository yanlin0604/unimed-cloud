package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.manager.PatientProfileManager;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 后台患者档案管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者档案")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/patient")
public class PatientProfileController extends BaseController {

    private final IChPatientProfileService patientProfileService;
    private final PatientProfileManager patientProfileManager;

    /**
     * 患者分页查询
     */
    @Operation(summary = "分页查询患者")
    @SaCheckPermission("chronic:patient:list")
    @GetMapping("/page")
    public TableDataInfo<ChPatientProfileVo> page(ChPatientProfileBo bo, PageQuery pageQuery) {
        return patientProfileService.queryPageList(bo, pageQuery);
    }

    /**
     * 新建患者档案
     */
    @Operation(summary = "新建患者档案")
    @SaCheckPermission("chronic:patient:add")
    @Log(title = "患者档案", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Long> add(@Validated @RequestBody ChPatientProfileBo bo) {
        Long patientId = patientProfileManager.createArchive(bo, Collections.emptyList(), Collections.emptyList());
        return R.ok(patientId);
    }

    /**
     * 患者详情
     */
    @Operation(summary = "患者详情")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/{patientId}")
    public R<ChPatientDetailVo> detail(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(patientProfileService.queryDetailById(patientId));
    }

    /**
     * 修改患者档案
     */
    @Operation(summary = "修改患者档案")
    @SaCheckPermission("chronic:patient:edit")
    @Log(title = "患者档案", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{patientId}")
    public R<Void> edit(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChPatientProfileBo bo) {
        bo.setPatientId(patientId);
        return toAjax(patientProfileService.updateByBo(bo));
    }

    /**
     * 患者时间线
     */
    @Operation(summary = "患者时间线")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/{patientId}/timeline")
    public R<List<ChPatientTimelineVo>> timeline(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(patientProfileService.queryTimelineByPatientId(patientId));
    }
}
