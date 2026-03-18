package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.portal.PortalProfileUpdateBo;
import org.dromara.dhcore.domain.vo.DhUserDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalUserProfileVo;
import org.dromara.dhcore.domain.vo.portal.PortalUserStatsVo;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.service.IDhUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * C端用户信息控制器
 * <p>
 * 提供当前登录用户的个人资料查询/修改、统计概览等接口。
 * 所有接口通过 LoginHelper.getUserId() 硬绑定当前用户，不接受外部 userId 参数。
 *
 * @author unimed
 */
@Tag(name = "C端-用户信息")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/user")
public class PortalUserController extends BaseController {

    private final IDhUserService dhUserService;
    private final DhUserProfileMapper userProfileMapper;

    /**
     * 获取当前用户个人资料
     */
    @Operation(summary = "获取个人资料")
    @GetMapping("/profile")
    public R<PortalUserProfileVo> getProfile() {
        Long userId = LoginHelper.getUserId();
        DhUserDetailVo detail = dhUserService.queryUserDetail(userId);
        if (detail == null) {
            return R.fail("用户信息不存在");
        }
        PortalUserProfileVo vo = new PortalUserProfileVo();
        vo.setUserId(detail.getId());
        vo.setUserName(detail.getUserName());
        vo.setPhone(detail.getPhone());
        vo.setAvatar(detail.getAvatar());
        vo.setMemberLevel(detail.getMemberLevel());
        vo.setWalletBalance(detail.getWalletBalance());
        vo.setRegisterTime(detail.getRegisterTime());
        vo.setStatus(detail.getStatus());
        return R.ok(vo);
    }

    /**
     * 修改当前用户个人资料
     */
    @Operation(summary = "修改个人资料")
    @PutMapping("/profile")
    public R<Void> updateProfile(@Validated @RequestBody PortalProfileUpdateBo bo) {
        Long userId = LoginHelper.getUserId();
        DhUserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) {
            return R.fail("用户信息不存在");
        }
        // 仅允许修改安全字段
        if (bo.getUserName() != null) {
            profile.setUserName(bo.getUserName());
        }
        if (bo.getAvatar() != null) {
            profile.setAvatar(bo.getAvatar());
        }
        if (bo.getPhone() != null) {
            profile.setPhone(bo.getPhone());
        }
        userProfileMapper.updateById(profile);
        return R.ok();
    }

    /**
     * 获取当前用户统计概览
     */
    @Operation(summary = "获取统计概览")
    @GetMapping("/stats")
    public R<PortalUserStatsVo> getStats() {
        Long userId = LoginHelper.getUserId();
        DhUserDetailVo detail = dhUserService.queryUserDetail(userId);
        if (detail == null) {
            return R.fail("用户信息不存在");
        }
        PortalUserStatsVo stats = new PortalUserStatsVo();
        stats.setOrderCount(detail.getOrderCount() != null ? detail.getOrderCount() : 0);
        stats.setTotalConsume(detail.getTotalConsume() != null ? detail.getTotalConsume() : BigDecimal.ZERO);
        stats.setTotalTopup(detail.getTotalTopup() != null ? detail.getTotalTopup() : BigDecimal.ZERO);
        // completedCount 和 worksCount 需要从订单统计获取，暂设默认值
        stats.setCompletedCount(0);
        stats.setWorksCount(0);
        return R.ok(stats);
    }
}
