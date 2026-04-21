package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.bo.ChScreeningRecordBo;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.chronic.domain.vo.ChScreeningRecordVo;
import org.dromara.chronic.manager.ScreeningManager;
import org.dromara.chronic.service.IChScreeningRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台义诊筛查管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-筛查批次")
@Validated
@RestController
@RequiredArgsConstructor
public class ScreeningBatchController {

    private final ScreeningManager screeningManager;
    private final IChScreeningRecordService screeningRecordService;

    @Operation(summary = "新增筛查批次")
    @SaCheckPermission("chronic:screening-batch:add")
    @Log(title = "筛查批次", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/screening-batch")
    public R<ChScreeningBatchVo> addBatch(@Validated @RequestBody ChScreeningBatchBo bo) {
        return R.ok(screeningManager.startBatch(bo));
    }

    @Operation(summary = "查询批次筛查记录")
    @SaCheckPermission("chronic:screening-batch:query")
    @GetMapping("/chronic/admin/screening-batch/{batchId}/records")
    public R<List<ChScreeningRecordVo>> batchRecords(@Parameter(description = "批次ID") @PathVariable Long batchId) {
        return R.ok(screeningRecordService.queryByBatchId(batchId));
    }

    @Operation(summary = "分页查询筛查记录")
    @SaCheckPermission("chronic:screening-record:list")
    @GetMapping("/chronic/admin/screening-record/page")
    public TableDataInfo<ChScreeningRecordVo> page(ChScreeningRecordBo bo, PageQuery pageQuery) {
        return screeningRecordService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "筛查确认入组")
    @SaCheckPermission("chronic:screening-record:enroll")
    @Log(title = "筛查入组", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/screening-record/{recordId}/enroll")
    public R<Long> enroll(@Parameter(description = "记录ID") @PathVariable Long recordId) {
        return R.ok(screeningManager.enroll(recordId));
    }
}
