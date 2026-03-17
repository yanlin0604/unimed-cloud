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
import org.dromara.dhcore.domain.bo.DhBalanceAdjustBo;
import org.dromara.dhcore.domain.bo.DhUserQueryBo;
import org.dromara.dhcore.domain.bo.DhUserStatusBo;
import org.dromara.dhcore.domain.bo.DhWalletLogQueryBo;
import org.dromara.dhcore.domain.vo.DhUserDetailVo;
import org.dromara.dhcore.domain.vo.DhUserItemVo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;
import org.dromara.dhcore.service.IDhUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人口播用户管理控制器
 */
@Tag(name = "数字人口�?用户管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/user")
public class DhUserController extends BaseController {

    private final IDhUserService dhUserService;

    @Operation(summary = "分页查询用户")
    @SaCheckPermission("dh:user:list")
    @GetMapping("/list")
    public TableDataInfo<DhUserItemVo> list(DhUserQueryBo bo, PageQuery pageQuery) {
        return dhUserService.queryUserPage(bo, pageQuery);
    }

    @Operation(summary = "查询用户详情")
    @SaCheckPermission("dh:user:detail")
    @GetMapping("/{userId}")
    public R<DhUserDetailVo> detail(@NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        return R.ok(dhUserService.queryUserDetail(userId));
    }

    @Operation(summary = "切换用户状�?)
    @SaCheckPermission("dh:user:status")
    @PostMapping("/status")
    public R<DhUserItemVo> changeStatus(@Validated @RequestBody DhUserStatusBo bo) {
        return R.ok(dhUserService.changeUserStatus(bo));
    }

    @Operation(summary = "调整用户余额")
    @SaCheckPermission("dh:user:adjust")
    @PostMapping("/adjust")
    public R<DhUserItemVo> adjust(@Validated @RequestBody DhBalanceAdjustBo bo) {
        return R.ok(dhUserService.adjustBalance(bo));
    }

    @Operation(summary = "分页查询钱包流水")
    @SaCheckPermission("dh:user:detail")
    @GetMapping("/wallet/list")
    public TableDataInfo<DhWalletLogVo> walletList(DhWalletLogQueryBo bo, PageQuery pageQuery) {
        return dhUserService.queryWalletLogPage(bo, pageQuery);
    }
}
