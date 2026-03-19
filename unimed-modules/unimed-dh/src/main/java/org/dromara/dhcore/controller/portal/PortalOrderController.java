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
import org.dromara.dhcore.domain.bo.portal.PortalOrderCreateBo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderVo;
import org.dromara.dhcore.service.IPortalOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * C端订单控制器
 * <p>
 * 提供当前登录用户的订单创建、列表查询、详情查看、取消订单、统计等接口。
 * 所有接口通过 LoginHelper.getUserId() 硬绑定当前用户。
 *
 * @author unimed
 */
@Tag(name = "C端-订单管理")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/order")
public class PortalOrderController extends BaseController {

    private final IPortalOrderService portalOrderService;

    // ==================== 查询端点 ====================

    /**
     * 分页查询当前用户订单列表
     *
     * @param status    可选状态筛选
     * @param keyword   可选关键词搜索（订单号/标题）
     * @param pageQuery 分页参数
     */
    @Operation(summary = "查询订单列表")
    @GetMapping("/list")
    public TableDataInfo<PortalOrderVo> list(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        return portalOrderService.listOrders(userId, status, keyword, pageQuery);
    }

    /**
     * 查询订单详情（验证归属权）
     */
    @Operation(summary = "查询订单详情")
    @GetMapping("/{orderId}")
    public R<PortalOrderDetailVo> detail(@PathVariable Long orderId) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalOrderService.getOrderDetail(userId, orderId));
    }

    /**
     * 获取当前用户订单统计
     */
    @Operation(summary = "订单统计")
    @GetMapping("/stats")
    public R<Map<String, Long>> stats() {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalOrderService.getOrderStats(userId));
    }

    // ==================== 写入端点 ====================

    /**
     * C端用户创建订单
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public R<PortalOrderVo> create(@Validated @RequestBody PortalOrderCreateBo bo) {
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getUsername();
        return R.ok(portalOrderService.createOrder(userId, userName, bo));
    }

    /**
     * C端用户取消订单
     * <p>
     * 仅 PENDING 状态可取消，取消后退还余额。
     */
    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public R<Void> cancel(@PathVariable Long orderId,
                          @RequestParam(required = false) String reason) {
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getUsername();
        portalOrderService.cancelOrder(userId, userName, orderId, reason);
        return R.ok();
    }
}