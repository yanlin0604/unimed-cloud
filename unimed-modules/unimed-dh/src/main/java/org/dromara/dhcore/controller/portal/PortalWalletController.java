package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.bo.DhWalletLogQueryBo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;
import org.dromara.dhcore.domain.vo.portal.PortalWalletBalanceVo;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.service.IDhUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * C端钱包控制器
 * <p>
 * 提供当前登录用户的钱包余额查询、流水分页查询等接口。
 * 所有接口通过 LoginHelper.getUserId() 硬绑定当前用户。
 *
 * @author unimed
 */
@Tag(name = "C端-钱包管理")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/wallet")
public class PortalWalletController extends BaseController {

    private final IDhUserService dhUserService;
    private final DhUserProfileMapper userProfileMapper;

    /**
     * 获取当前用户钱包余额
     */
    @Operation(summary = "获取钱包余额")
    @GetMapping("/balance")
    public R<PortalWalletBalanceVo> getBalance() {
        Long userId = LoginHelper.getUserId();
        DhUserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) {
            return R.fail("用户信息不存在");
        }
        PortalWalletBalanceVo vo = new PortalWalletBalanceVo();
        vo.setBalance(profile.getWalletBalance() != null ? profile.getWalletBalance() : BigDecimal.ZERO);
        vo.setTotalTopup(profile.getTotalTopup() != null ? profile.getTotalTopup() : BigDecimal.ZERO);
        vo.setTotalConsume(profile.getTotalConsume() != null ? profile.getTotalConsume() : BigDecimal.ZERO);
        // totalRefund 暂无对应字段，设默认值
        vo.setTotalRefund(BigDecimal.ZERO);
        return R.ok(vo);
    }

    /**
     * 分页查询当前用户钱包流水
     */
    @Operation(summary = "查询钱包流水")
    @GetMapping("/logs")
    public TableDataInfo<DhWalletLogVo> getWalletLogs(DhWalletLogQueryBo bo, PageQuery pageQuery) {
        // 硬绑定当前用户ID，防止越权查询
        bo.setUserId(LoginHelper.getUserId());
        return dhUserService.queryWalletLogPage(bo, pageQuery);
    }
}
