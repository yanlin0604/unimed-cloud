package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.chronic.manager.EncounterManager;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端诊疗记录
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端诊疗记录")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor")
public class DoctorEncounterController extends BaseController {

    private final EncounterManager encounterManager;
    private final DoctorScopeGuard doctorScopeGuard;

    /**
     * 解析诊疗记录归属患者并校验
     * <p>
     * edit/submit/detail 的路径参数是 encounterId，原实现零校验。
     * submit 传入的 operatorId 只是署名（写时间线与审计），不构成鉴权。
     */
    private void assertEncounterOwned(Long encounterId) {
        ChEncounterRecordVo record = encounterManager.queryById(encounterId);
        doctorScopeGuard.assertRecordOwned(record == null ? null : record.getPatientId());
    }

    @Operation(summary = "新增诊疗记录")
    @SaCheckPermission("chronic:doctor:encounter:add")
    @Log(title = "诊疗记录", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/encounter")
    public R<Long> add(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChEncounterRecordBo bo) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        bo.setSourceType("DOCTOR");
        return R.ok(encounterManager.saveDraft(bo, bo.getDiagnosisList()));
    }

    @Operation(summary = "修改诊疗记录")
    @SaCheckPermission("chronic:doctor:encounter:edit")
    @Log(title = "诊疗记录", businessType = BusinessType.UPDATE)
    @PutMapping("/encounter/{encounterId}")
    public R<Long> edit(@PathVariable Long encounterId, @Validated @RequestBody ChEncounterRecordBo bo) {
        assertEncounterOwned(encounterId);
        bo.setId(encounterId);
        return R.ok(encounterManager.updateDraft(bo, bo.getDiagnosisList()));
    }

    @Operation(summary = "提交诊疗记录")
    @SaCheckPermission("chronic:doctor:encounter:submit")
    @Log(title = "诊疗记录", businessType = BusinessType.UPDATE)
    @PostMapping("/encounter/{encounterId}/submit")
    public R<Long> submit(@PathVariable Long encounterId) {
        assertEncounterOwned(encounterId);
        return R.ok(encounterManager.submit(encounterId, LoginHelper.getUserId()));
    }

    @Operation(summary = "患者诊疗记录列表")
    @SaCheckPermission("chronic:doctor:encounter:list")
    @GetMapping("/patient/{patientId}/encounter/page")
    public TableDataInfo<ChEncounterRecordVo> page(@PathVariable Long patientId, ChEncounterRecordBo bo, PageQuery pageQuery) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        return encounterManager.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "诊疗记录详情")
    @SaCheckPermission("chronic:doctor:encounter:query")
    @GetMapping("/encounter/{encounterId}")
    public R<ChEncounterRecordVo> detail(@PathVariable Long encounterId) {
        assertEncounterOwned(encounterId);
        return R.ok(encounterManager.queryById(encounterId));
    }
}