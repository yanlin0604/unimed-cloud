package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhDialectInviteBo;
import org.dromara.dhcore.domain.bo.DhDialectInviteQueryBo;
import org.dromara.dhcore.domain.vo.DhDialectInviteVo;
import org.dromara.dhcore.service.IDhDialectInviteService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 方言邀请码配置管理接口
 *
 * @author unimed
 */
@Tag(name = "方言邀请码配置管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/dialectInvite")
public class DhDialectInviteController extends BaseController {

    private final IDhDialectInviteService dialectInviteService;

    /**
     * 分页查询邀请码配置列表
     */
    @Operation(summary = "分页查询邀请码配置列表")
    @SaCheckPermission("dh:dialectInvite:list")
    @GetMapping("/list")
    public TableDataInfo<DhDialectInviteVo> list(DhDialectInviteQueryBo queryBo, PageQuery pageQuery) {
        return dialectInviteService.queryPage(queryBo, pageQuery);
    }

    /**
     * 新增邀请码配置
     */
    @Operation(summary = "新增邀请码配置")
    @SaCheckPermission("dh:dialectInvite:add")
    @Log(title = "方言邀请码配置", businessType = BusinessType.INSERT)
    @PostMapping
    public R<DhDialectInviteVo> add(@Validated(AddGroup.class) @RequestBody DhDialectInviteBo bo) {
        return R.ok(dialectInviteService.save(bo));
    }

    /**
     * 修改邀请码配置
     */
    @Operation(summary = "修改邀请码配置")
    @SaCheckPermission("dh:dialectInvite:edit")
    @Log(title = "方言邀请码配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<DhDialectInviteVo> edit(@Validated(EditGroup.class) @RequestBody DhDialectInviteBo bo) {
        return R.ok(dialectInviteService.update(bo));
    }

    /**
     * 删除邀请码配置
     */
    @Operation(summary = "删除邀请码配置")
    @SaCheckPermission("dh:dialectInvite:remove")
    @Log(title = "方言邀请码配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{inviteIds}")
    public R<Void> remove(@NotEmpty(message = "请选择要删除的邀请码配置") @PathVariable List<Long> inviteIds) {
        dialectInviteService.deleteByIds(inviteIds);
        return R.ok();
    }
}
