package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.manager.WarningManager;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 预警事件管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-预警事件")
@Validated
@RestController
@RequiredArgsConstructor
public class WarningController {

    private final WarningManager warningManager;
    private final IChWarningEventService warningEventService;

    @Operation(summary = "分页查询预警事件")
    @SaCheckPermission("chronic:warning:list")
    @GetMapping("/chronic/admin/warning/page")
    public TableDataInfo<ChWarningEventVo> page(ChWarningEventBo bo, PageQuery pageQuery) {
        return warningEventService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "预警事件详情")
    @SaCheckPermission("chronic:warning:query")
    @GetMapping("/chronic/admin/warning/{warningId}")
    public R<ChWarningEventVo> detail(
        @Parameter(description = "预警事件ID") @PathVariable Long warningId) {
        return R.ok(warningManager.queryDetail(warningId));
    }

    @Operation(summary = "处理预警事件")
    @SaCheckPermission("chronic:warning:handle")
    @PutMapping("/chronic/admin/warning/{warningId}/action")
    public R<Void> handle(
        @Parameter(description = "预警事件ID") @PathVariable Long warningId,
        @Parameter(description = "处理操作类型") @RequestParam String actionType,
        @Parameter(description = "处理详情") @RequestParam(required = false) String actionDetail) {
        Long userId = null;
        return R.ok(warningManager.handleEvent(warningId, actionType, actionDetail, userId));
    }
}
