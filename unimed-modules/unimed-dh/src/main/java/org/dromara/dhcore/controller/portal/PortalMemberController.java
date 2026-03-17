package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.bo.DhMemberConfigQueryBo;
import org.dromara.dhcore.domain.vo.DhMemberConfigVo;
import org.dromara.dhcore.domain.vo.portal.PortalMemberInfoVo;
import org.dromara.dhcore.domain.vo.portal.PortalMemberLevelVo;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.service.IDhConfigService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * C端会员信息控制器
 * <p>
 * 提供当前登录用户的会员信息查询、公开会员等级列表查询等接口�?
 * 所有接口通过 LoginHelper.getUserId() 硬绑定当前用户�?
 *
 * @author unimed
 */
@Tag(name = "C�?会员信息")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/member")
public class PortalMemberController extends BaseController {

    private final DhUserProfileMapper userProfileMapper;
    private final IDhConfigService configService;

    /**
     * 获取当前用户会员信息
     */
    @Operation(summary = "获取我的会员信息")
    @GetMapping("/info")
    public R<PortalMemberInfoVo> getMemberInfo() {
        Long userId = LoginHelper.getUserId();
        DhUserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) {
            return R.fail("用户信息不存�?);
        }
        PortalMemberInfoVo vo = new PortalMemberInfoVo();
        vo.setLevel(profile.getMemberLevel());

        // 查询对应会员配置获取等级详情
        DhMemberConfigVo config = findMemberConfig(profile.getMemberLevel());
        if (config != null) {
            vo.setLevelName(config.getLevelName());
            vo.setOrderPrice(config.getOrderPrice());
            vo.setMonthlyLimit(config.getMonthlyLimit());
            vo.setSpeedPriority(config.getSpeedPriority());
            vo.setExpectDeliveryHours(config.getExpectDeliveryHours());
            vo.setRedoLimit(config.getRedoLimit());
        } else {
            vo.setLevelName(profile.getMemberLevel());
        }
        return R.ok(vo);
    }

    /**
     * 获取所有公开会员等级列表
     */
    @Operation(summary = "获取会员等级列表")
    @GetMapping("/levels")
    public R<List<PortalMemberLevelVo>> getMemberLevels() {
        // 查询所有启用状态的会员配置
        DhMemberConfigQueryBo queryBo = new DhMemberConfigQueryBo();
        queryBo.setStatus("0"); // 0=启用
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(100); // 会员等级数量有限，一次取�?
        TableDataInfo<DhMemberConfigVo> configPage = queryMemberConfigList(queryBo, pageQuery);

        List<PortalMemberLevelVo> levels = new ArrayList<>();
        if (configPage != null && configPage.getRows() != null) {
            for (DhMemberConfigVo config : configPage.getRows()) {
                PortalMemberLevelVo levelVo = new PortalMemberLevelVo();
                levelVo.setId(config.getId());
                levelVo.setLevel(config.getLevel());
                levelVo.setLevelName(config.getLevelName());
                levelVo.setOrderPrice(config.getOrderPrice());
                levelVo.setMonthlyLimit(config.getMonthlyLimit());
                levelVo.setSpeedPriority(config.getSpeedPriority());
                levelVo.setMinTopupAmount(config.getMinTopupAmount());
                levelVo.setValidityDays(config.getValidityDays());
                levelVo.setExpectDeliveryHours(config.getExpectDeliveryHours());
                levelVo.setRedoLimit(config.getRedoLimit());
                levels.add(levelVo);
            }
        }
        return R.ok(levels);
    }

    /**
     * 根据会员等级查询配置
     */
    private DhMemberConfigVo findMemberConfig(String level) {
        if (level == null) {
            return null;
        }
        DhMemberConfigQueryBo queryBo = new DhMemberConfigQueryBo();
        queryBo.setLevel(level);
        queryBo.setStatus("0");
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(1);
        TableDataInfo<DhMemberConfigVo> result = queryMemberConfigList(queryBo, pageQuery);
        if (result != null && result.getRows() != null && !result.getRows().isEmpty()) {
            return result.getRows().get(0);
        }
        return null;
    }

    /**
     * 委托查询会员配置分页
     */
    private TableDataInfo<DhMemberConfigVo> queryMemberConfigList(DhMemberConfigQueryBo bo, PageQuery pageQuery) {
        return configService.queryMemberConfigPage(bo, pageQuery);
    }
}
