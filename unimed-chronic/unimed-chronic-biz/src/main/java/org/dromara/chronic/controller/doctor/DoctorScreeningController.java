package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.bo.ChScreeningRecordBo;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.chronic.domain.vo.ChScreeningRecordVo;
import org.dromara.chronic.manager.ScreeningManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端义诊筛查
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端筛查")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/screening")
public class DoctorScreeningController {

    private final ScreeningManager screeningManager;

    @Operation(summary = "发起筛查")
    @SaCheckPermission("chronic:doctor:screening:start")
    @Log(title = "筛查批次", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/start")
    public R<ChScreeningBatchVo> start(@Validated @RequestBody ChScreeningBatchBo bo) {
        return R.ok(screeningManager.startBatch(bo));
    }

    @Operation(summary = "新增筛查记录")
    @SaCheckPermission("chronic:doctor:screening:record")
    @Log(title = "筛查记录", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/record")
    public R<ChScreeningRecordVo> record(@Validated @RequestBody ChScreeningRecordBo bo) {
        return R.ok(screeningManager.saveRecord(bo));
    }

    @Operation(summary = "批量补传筛查记录")
    @SaCheckPermission("chronic:doctor:screening:batch-upload")
    @Log(title = "筛查记录批量补传", businessType = BusinessType.IMPORT)
    @RepeatSubmit
    @PostMapping("/batch-upload")
    public R<List<ChScreeningRecordVo>> batchUpload(@RequestBody List<@Valid ChScreeningRecordBo> list) {
        return R.ok(screeningManager.batchUpload(list));
    }

    @Operation(summary = "确认入组")
    @SaCheckPermission("chronic:doctor:screening:enroll")
    @Log(title = "筛查确认入组", businessType = BusinessType.UPDATE)
    @PostMapping("/{recordId}/confirm-enroll")
    public R<Long> confirmEnroll(@Parameter(description = "记录ID", required = true) @PathVariable Long recordId) {
        return R.ok(screeningManager.enroll(recordId));
    }
}
