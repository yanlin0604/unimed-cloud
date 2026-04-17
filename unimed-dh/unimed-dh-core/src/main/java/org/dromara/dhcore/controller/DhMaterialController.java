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
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhMaterialBo;
import org.dromara.dhcore.domain.bo.DhMaterialQueryBo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.service.IDhMaterialService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * B端素材管理控制器
 *
 * @author unimed
 */
@Tag(name = "B端-素材管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/material")
public class DhMaterialController extends BaseController {

    private final IDhMaterialService dhMaterialService;

    /**
     * 分页查询系统素材列表
     */
    @Operation(summary = "查询素材列表")
    @SaCheckPermission("dh:material:list")
    @GetMapping("/list")
    public TableDataInfo<DhMaterialVo> list(DhMaterialQueryBo bo, PageQuery pageQuery) {
        return dhMaterialService.querySystemMaterialPage(bo, pageQuery);
    }

    /**
     * 新增系统素材
     */
    @Operation(summary = "新增素材")
    @SaCheckPermission("dh:material:add")
    @PostMapping
    public R<DhMaterialVo> add(@Validated @RequestBody DhMaterialBo bo) {
        return R.ok(dhMaterialService.saveSystemMaterial(bo));
    }

    /**
     * 修改系统素材
     */
    @Operation(summary = "修改素材")
    @SaCheckPermission("dh:material:edit")
    @PutMapping
    public R<DhMaterialVo> edit(@Validated @RequestBody DhMaterialBo bo) {
        return R.ok(dhMaterialService.updateSystemMaterial(bo));
    }

    /**
     * 删除系统素材
     *
     * @param materialId 素材ID
     */
    @Operation(summary = "删除素材")
    @SaCheckPermission("dh:material:remove")
    @DeleteMapping("/{materialId}")
    public R<Boolean> remove(@NotNull @PathVariable Long materialId) {
        return R.ok(dhMaterialService.deleteSystemMaterial(materialId));
    }

    /**
     * 切换素材状态
     */
    @Operation(summary = "切换素材状态")
    @SaCheckPermission("dh:material:status")
    @PostMapping("/status")
    public R<DhMaterialVo> changeStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhMaterialService.changeSystemMaterialStatus(bo));
    }
}
