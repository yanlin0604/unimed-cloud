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
import org.dromara.chronic.service.IChScreeningBatchService;
import cn.hutool.core.util.ObjectUtil;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
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
    private final IChScreeningBatchService screeningBatchService;

    @Operation(summary = "分页查询本人筛查批次")
    @SaCheckPermission("chronic:doctor:screening:start")
    @GetMapping("/batches")
    public TableDataInfo<ChScreeningBatchVo> batches(ChScreeningBatchBo bo, PageQuery pageQuery) {
        // 仅返回当前医生发起的批次（doctorUserId 一律服务端覆写，防止越权查看他人批次）
        bo.setDoctorUserId(LoginHelper.getUserId());
        return screeningBatchService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "本人筛查批次详情")
    @SaCheckPermission("chronic:doctor:screening:start")
    @GetMapping("/batches/{batchId}")
    public R<ChScreeningBatchVo> batchDetail(@Parameter(description = "批次ID", required = true) @PathVariable Long batchId) {
        ChScreeningBatchVo vo = screeningBatchService.queryById(batchId);
        if (vo == null) {
            throw new ServiceException("筛查批次不存在");
        }
        // 与 /batches 列表口径一致：医生端只能查看本人发起的批次
        if (!ObjectUtil.equal(vo.getDoctorUserId(), LoginHelper.getUserId())) {
            throw new ServiceException("无权查看该筛查批次");
        }
        return R.ok(vo);
    }

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
