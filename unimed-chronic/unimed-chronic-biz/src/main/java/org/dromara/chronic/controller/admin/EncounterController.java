package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChEncounterDiagnosisBo;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.chronic.manager.EncounterManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊疗记录管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-诊疗记录")
@Validated
@RestController
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterManager encounterManager;

    @Operation(summary = "新增诊疗记录（草稿）")
    @SaCheckPermission("chronic:encounter:add")
    @Log(title = "诊疗记录", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/encounter")
    public R<Long> add(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChEncounterRecordBo bo) {
        bo.setPatientId(patientId);
        return R.ok(encounterManager.saveDraft(bo, bo.getDiagnosisList()));
    }

    @Operation(summary = "修改诊疗记录（草稿）")
    @SaCheckPermission("chronic:encounter:edit")
    @Log(title = "诊疗记录", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/encounter/{encounterId}")
    public R<Long> edit(@Parameter(description = "诊疗记录ID") @PathVariable Long encounterId, @Validated @RequestBody ChEncounterRecordBo bo) {
        bo.setId(encounterId);
        return R.ok(encounterManager.updateDraft(bo, bo.getDiagnosisList()));
    }

    @Operation(summary = "提交诊疗记录")
    @SaCheckPermission("chronic:encounter:submit")
    @Log(title = "诊疗记录", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/encounter/{encounterId}/submit")
    public R<Long> submit(@Parameter(description = "诊疗记录ID") @PathVariable Long encounterId) {
        return R.ok(encounterManager.submit(encounterId, LoginHelper.getUserId()));
    }

    @Operation(summary = "分页查询诊疗记录")
    @SaCheckPermission("chronic:encounter:list")
    @GetMapping("/chronic/admin/patient/{patientId}/encounter/page")
    public TableDataInfo<ChEncounterRecordVo> page(@PathVariable Long patientId, ChEncounterRecordBo bo, PageQuery pageQuery) {
        bo.setPatientId(patientId);
        return encounterManager.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "诊疗记录详情")
    @SaCheckPermission("chronic:encounter:query")
    @GetMapping("/chronic/admin/encounter/{encounterId}")
    public R<ChEncounterRecordVo> detail(@Parameter(description = "诊疗记录ID") @PathVariable Long encounterId) {
        return R.ok(encounterManager.queryById(encounterId));
    }
}