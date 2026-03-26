package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptQueryBo;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;
import org.dromara.dhcore.service.IDhDialectPromptService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B端采集文字管理控制器
 *
 * @author unimed
 */
@Tag(name = "B端-采集文字管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/dialect")
public class DhDialectPromptController extends BaseController {

    private final IDhDialectPromptService dialectPromptService;

    /**
     * 分页查询提示文字列表
     */
    @Operation(summary = "查询提示文字列表")
    @SaCheckPermission("dh:dialect:list")
    @GetMapping("/list")
    public TableDataInfo<DhDialectPromptVo> list(DhDialectPromptQueryBo bo, PageQuery pageQuery) {
        return dialectPromptService.queryPage(bo, pageQuery);
    }

    /**
     * 新增提示文字
     */
    @Operation(summary = "新增提示文字")
    @SaCheckPermission("dh:dialect:add")
    @PostMapping
    public R<DhDialectPromptVo> add(@Validated @RequestBody DhDialectPromptBo bo) {
        return R.ok(dialectPromptService.save(bo));
    }

    /**
     * 修改提示文字
     */
    @Operation(summary = "修改提示文字")
    @SaCheckPermission("dh:dialect:edit")
    @PutMapping
    public R<DhDialectPromptVo> edit(@Validated @RequestBody DhDialectPromptBo bo) {
        return R.ok(dialectPromptService.update(bo));
    }

    /**
     * 删除提示文字
     */
    @Operation(summary = "删除提示文字")
    @SaCheckPermission("dh:dialect:remove")
    @DeleteMapping("/{promptIds}")
    public R<Boolean> remove(@PathVariable List<Long> promptIds) {
        return R.ok(dialectPromptService.delete(promptIds));
    }

    /**
     * 切换提示文字状态
     */
    @Operation(summary = "切换提示文字状态")
    @SaCheckPermission("dh:dialect:edit")
    @PostMapping("/status")
    public R<DhDialectPromptVo> changeStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dialectPromptService.changeStatus(bo));
    }
}
