package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhBackgroundBo;
import org.dromara.dhcore.domain.bo.DhBackgroundQueryBo;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.vo.DhBackgroundVo;
import org.dromara.dhcore.service.IDhBackgroundService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * B端背景管理控制器
 *
 * @author unimed
 */
@Tag(name = "B端-背景管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/background")
public class DhBackgroundController extends BaseController {

    private final IDhBackgroundService dhBackgroundService;

    /**
     * 分页查询背景列表
     */
    @Operation(summary = "查询背景列表")
    @SaCheckPermission("dh:background:list")
    @GetMapping("/list")
    public TableDataInfo<DhBackgroundVo> list(DhBackgroundQueryBo bo, PageQuery pageQuery) {
        return dhBackgroundService.queryPage(bo, pageQuery);
    }

    /**
     * 新增背景
     */
    @Operation(summary = "新增背景")
    @SaCheckPermission("dh:background:add")
    @PostMapping
    public R<DhBackgroundVo> add(@Validated @RequestBody DhBackgroundBo bo) {
        return R.ok(dhBackgroundService.save(bo));
    }

    /**
     * 修改背景
     */
    @Operation(summary = "修改背景")
    @SaCheckPermission("dh:background:edit")
    @PutMapping
    public R<DhBackgroundVo> edit(@Validated @RequestBody DhBackgroundBo bo) {
        return R.ok(dhBackgroundService.update(bo));
    }

    /**
     * 删除背景
     *
     * @param backgroundId 背景ID
     */
    @Operation(summary = "删除背景")
    @SaCheckPermission("dh:background:remove")
    @DeleteMapping("/{backgroundId}")
    public R<Boolean> remove(@NotNull @PathVariable Long backgroundId) {
        return R.ok(dhBackgroundService.delete(backgroundId));
    }

    /**
     * 切换背景状态
     */
    @Operation(summary = "切换背景状态")
    @SaCheckPermission("dh:background:status")
    @PostMapping("/status")
    public R<DhBackgroundVo> changeStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhBackgroundService.changeStatus(bo));
    }
}
