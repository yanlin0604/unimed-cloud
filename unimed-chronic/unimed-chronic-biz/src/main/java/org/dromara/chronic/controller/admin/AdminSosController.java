package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChSosRecordBo;
import org.dromara.chronic.domain.vo.ChSosRecordVo;
import org.dromara.chronic.service.IChSosRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 SOS 紧急求助管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-管理端SOS求助")
@Validated
@RestController
@RequiredArgsConstructor
public class AdminSosController extends BaseController {

    private final IChSosRecordService sosRecordService;

    /**
     * 分页查询 SOS 求助记录列表
     */
    @Operation(summary = "查询SOS求助记录列表")
    @SaCheckPermission("chronic:sos:list")
    @GetMapping("/chronic/admin/sos/page")
    public TableDataInfo<ChSosRecordVo> page(ChSosRecordBo bo, PageQuery pageQuery) {
        return sosRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取 SOS 求助记录详情
     */
    @Operation(summary = "获取SOS求助记录详情")
    @SaCheckPermission("chronic:sos:query")
    @GetMapping("/chronic/admin/sos/{sosId}")
    public R<ChSosRecordVo> getInfo(@Parameter(description = "求助ID", required = true) @PathVariable Long sosId) {
        return R.ok(sosRecordService.queryById(sosId));
    }

    /**
     * 处置 SOS 求助记录
     */
    @Operation(summary = "处置SOS求助记录")
    @SaCheckPermission("chronic:sos:edit")
    @Log(title = "SOS紧急求助处置", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/sos/{sosId}/handle")
    public R<Void> handle(
            @Parameter(description = "求助ID", required = true) @PathVariable Long sosId,
            @Parameter(description = "事件状态(RESOLVED/FALSE_ALARM/HANDLING)", required = true) @RequestParam String eventStatus,
            @Parameter(description = "处置备注") @RequestParam(required = false) String handleRemark) {
        Long handlerUserId = LoginHelper.getUserId();
        sosRecordService.handleSos(sosId, handlerUserId, eventStatus, handleRemark);
        return R.ok();
    }
}
