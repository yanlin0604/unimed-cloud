package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChPatientProfileImportVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.listener.ChPatientProfileImportListener;
import org.dromara.chronic.manager.PatientProfileManager;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.domain.R;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
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
     * 校验身份证唯一性
     */
    @Operation(summary = "校验身份证号唯一性")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/check-idcard")
    public R<Boolean> checkIdCard(@RequestParam String idCard, @RequestParam(required = false) Long excludeId) {
        return R.ok(patientProfileService.checkIdCardUnique(idCard, excludeId));
    }

    /**
     * 导出患者档案
     */
    @Log(title = "患者档案", businessType = BusinessType.EXPORT)
    @SaCheckPermission("chronic:patient:export")
    @PostMapping("/export")
    public void export(ChPatientProfileBo bo, HttpServletResponse response) {
        List<ChPatientProfileVo> list = patientProfileService.queryList(bo);
        ExcelUtil.exportExcel(list, "患者档案", ChPatientProfileVo.class, response);
    }

    /**
     * 获取导入模板
     */
    @Operation(summary = "获取导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "患者档案数据", ChPatientProfileImportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Operation(summary = "导入患者档案数据")
    @Log(title = "患者档案管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("chronic:patient:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Void> importData(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<ChPatientProfileImportVo> result = ExcelUtil.importExcel(file.getInputStream(), ChPatientProfileImportVo.class, new ChPatientProfileImportListener(updateSupport));
        return R.ok(result.getAnalysis());
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
        Long patientId = patientProfileManager.createArchive(
            bo,
            patientProfileManager.convertDiseases(bo.getDiseases()),
            patientProfileManager.convertTags(bo.getTags())
        );
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
        patientProfileManager.editArchive(bo);
        return R.ok();
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

    /**
     * 删除患者档案
     */
    @Operation(summary = "删除患者档案")
    @SaCheckPermission("chronic:patient:remove")
    @Log(title = "患者档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{patientIds}")
    public R<Void> remove(@Parameter(description = "患者ID集合") @PathVariable Long[] patientIds) {
        if (patientIds == null || patientIds.length == 0) {
            return R.fail("patientIds 不能为空");
        }
        return toAjax(patientProfileService.deleteByIds(java.util.Arrays.asList(patientIds)));
    }

    // ==================== 病种粒度操作 ====================

    /**
     * 新增单条病种
     */
    @Operation(summary = "新增患者病种")
    @SaCheckPermission("chronic:patient:edit")
    @Log(title = "患者病种", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/disease")
    public R<Long> addDisease(@Validated @RequestBody ChPatientDiseaseBo bo) {
        return R.ok(patientProfileManager.bindDisease(bo));
    }

    /**
     * 修改单条病种
     */
    @Operation(summary = "修改患者病种")
    @SaCheckPermission("chronic:patient:edit")
    @Log(title = "患者病种", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/disease")
    public R<Void> editDisease(@Validated @RequestBody ChPatientDiseaseBo bo) {
        patientProfileManager.updateDisease(bo);
        return R.ok();
    }

    /**
     * 切换病种启停状态
     */
    @Operation(summary = "切换病种启停状态")
    @SaCheckPermission("chronic:patient:edit")
    @Log(title = "患者病种状态", businessType = BusinessType.UPDATE)
    @PutMapping("/disease/{id}/status")
    public R<Void> toggleDiseaseStatus(@Parameter(description = "病种主键") @PathVariable Long id,
                                       @Parameter(description = "目标状态") @RequestParam Boolean enableStatus) {
        patientProfileManager.toggleDiseaseStatus(id, enableStatus);
        return R.ok();
    }

    /**
     * 移除单条病种
     */
    @Operation(summary = "移除患者病种")
    @SaCheckPermission("chronic:patient:edit")
    @Log(title = "患者病种", businessType = BusinessType.DELETE)
    @DeleteMapping("/disease/{id}")
    public R<Void> removeDisease(@Parameter(description = "病种主键") @PathVariable Long id) {
        patientProfileManager.removeDisease(id);
        return R.ok();
    }
}
