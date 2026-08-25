package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupDispatchPoolBo;
import org.dromara.chronic.domain.bo.ChFollowupDispatchRunBo;
import org.dromara.chronic.domain.vo.ChFollowupDispatchPoolVo;
import org.dromara.chronic.domain.vo.ChFollowupDispatchResultVo;
import org.dromara.chronic.service.IChFollowupDispatchPoolService;
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
 * 随访任务自动分发人员池管理控制器
 *
 * @author unimed
 */
@Tag(name = "随访管理-自动分发人员池")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/followup/dispatch-pool")
public class FollowupDispatchPoolController extends BaseController {

    private final IChFollowupDispatchPoolService dispatchPoolService;

    @Operation(summary = "分页查询分发人员池")
    @SaCheckPermission("chronic:followup-task:pool")
    @GetMapping("/page")
    public TableDataInfo<ChFollowupDispatchPoolVo> page(ChFollowupDispatchPoolBo bo, PageQuery pageQuery) {
        return dispatchPoolService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "查询所有启用的分发人员列表")
    @SaCheckPermission("chronic:followup-task:pool")
    @GetMapping("/active-list")
    public R<List<ChFollowupDispatchPoolVo>> activeList() {
        return R.ok(dispatchPoolService.queryActiveList());
    }

    @Operation(summary = "添加人员到分发人员池")
    @SaCheckPermission("chronic:followup-task:batch-assign")
    @Log(title = "随访分发人员池", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/add")
    public R<Void> addUsers(@Validated @RequestBody ChFollowupDispatchPoolBo bo) {
        return toAjax(dispatchPoolService.addUsersToPool(bo));
    }

    @Operation(summary = "修改分发人员配置")
    @SaCheckPermission("chronic:followup-task:batch-assign")
    @Log(title = "随访分发人员池", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{id}")
    public R<Void> updateMember(@Parameter(description = "ID") @PathVariable Long id, @Validated @RequestBody ChFollowupDispatchPoolBo bo) {
        bo.setId(id);
        return toAjax(dispatchPoolService.updatePoolMember(bo));
    }

    @Operation(summary = "切换分发人员接单状态")
    @SaCheckPermission("chronic:followup-task:batch-assign")
    @Log(title = "随访分发人员池", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public R<Void> toggleStatus(@Parameter(description = "ID") @PathVariable Long id, @RequestParam Boolean isActive) {
        return toAjax(dispatchPoolService.toggleActive(id, isActive));
    }

    @Operation(summary = "从分发人员池移除")
    @SaCheckPermission("chronic:followup-task:batch-assign")
    @Log(title = "随访分发人员池", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> removeMembers(@Parameter(description = "ID列表") @PathVariable List<Long> ids) {
        return toAjax(dispatchPoolService.removeFromPool(ids));
    }

    @Operation(summary = "立即执行自动跑批/随机分发任务")
    @SaCheckPermission("chronic:followup-task:batch-assign")
    @Log(title = "随访任务跑批分发", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/run-batch-dispatch")
    public R<ChFollowupDispatchResultVo> runBatchDispatch(@RequestBody ChFollowupDispatchRunBo bo) {
        return R.ok(dispatchPoolService.executeBatchDispatch(bo));
    }
}
