package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientCloseApplyBo;
import org.dromara.chronic.domain.vo.ChPatientCloseApplyVo;
import org.dromara.chronic.service.IChPatientCloseApplyService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台患者结案申请管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者结案申请")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/patient/close-case")
public class PatientCloseCaseController extends BaseController {

    private final IChPatientCloseApplyService closeApplyService;

    /**
     * 发起结案申请
     */
    @Operation(summary = "发起结案申请")
    @SaCheckPermission("patient:closeCase:add")
    @Log(title = "患者结案申请", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/apply")
    public R<Long> apply(@Validated(ChPatientCloseApplyBo.ApplyGroup.class) @RequestBody ChPatientCloseApplyBo bo) {
        return R.ok(closeApplyService.applyClose(bo));
    }

    /**
     * 审核结案申请
     */
    @Operation(summary = "审核结案申请")
    @SaCheckPermission("patient:closeCase:audit")
    @Log(title = "患者结案审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public R<Void> audit(@Validated(ChPatientCloseApplyBo.AuditGroup.class) @RequestBody ChPatientCloseApplyBo bo) {
        closeApplyService.auditClose(bo);
        return R.ok();
    }

    /**
     * 分页查询结案申请
     */
    @Operation(summary = "分页查询结案申请")
    @SaCheckPermission("patient:closeCase:list")
    @GetMapping("/page")
    public TableDataInfo<ChPatientCloseApplyVo> page(ChPatientCloseApplyBo bo, PageQuery pageQuery) {
        return closeApplyService.queryPageList(bo, pageQuery);
    }

    /**
     * 结案申请详情
     */
    @Operation(summary = "结案申请详情")
    @SaCheckPermission("patient:closeCase:query")
    @GetMapping("/{applyId}")
    public R<ChPatientCloseApplyVo> detail(@Parameter(description = "申请ID") @PathVariable Long applyId) {
        return R.ok(closeApplyService.queryById(applyId));
    }

    /**
     * 查询某患者最新一条结案申请（用于列表回显状态）
     */
    @Operation(summary = "查询患者最新结案申请")
    @SaCheckPermission("patient:closeCase:query")
    @GetMapping("/latest/{patientId}")
    public R<ChPatientCloseApplyVo> latestByPatient(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(closeApplyService.queryLatestByPatient(patientId));
    }
}
