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
import org.dromara.dhcore.domain.bo.*;
import org.dromara.dhcore.domain.vo.DhOrderDetailVo;
import org.dromara.dhcore.domain.vo.DhOrderItemVo;
import org.dromara.dhcore.service.IDhOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数字人口播订单控制器
 */
@Tag(name = "数字人口播-订单")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/order")
public class DhOrderController extends BaseController {

    private final IDhOrderService dhOrderService;

    @Operation(summary = "分页查询订单")
    @SaCheckPermission("dh:order:list")
    @GetMapping("/list")
    public TableDataInfo<DhOrderItemVo> list(DhOrderQueryBo bo, PageQuery pageQuery) {
        return dhOrderService.queryOrderPage(bo, pageQuery);
    }

    @Operation(summary = "查询订单详情")
    @SaCheckPermission("dh:order:detail")
    @GetMapping("/{orderId}")
    public R<DhOrderDetailVo> detail(@NotNull(message = "订单ID不能为空") @PathVariable Long orderId) {
        return R.ok(dhOrderService.queryOrderDetail(orderId));
    }

    @Operation(summary = "领取订单")
    @SaCheckPermission("dh:order:claim")
    @PostMapping("/{orderId}/claim")
    public R<DhOrderDetailVo> claim(@NotNull(message = "订单ID不能为空") @PathVariable Long orderId,
                                    @RequestBody(required = false) DhOrderClaimBo bo) {
        return R.ok(dhOrderService.claimOrder(orderId, bo));
    }

    @Operation(summary = "取消订单")
    @SaCheckPermission("dh:order:cancel")
    @PostMapping("/cancel")
    public R<DhOrderDetailVo> cancel(@Validated @RequestBody DhOrderCancelBo bo) {
        return R.ok(dhOrderService.cancelOrder(bo));
    }

    @Operation(summary = "拒绝订单")
    @SaCheckPermission("dh:order:reject")
    @PostMapping("/reject")
    public R<DhOrderDetailVo> reject(@Validated @RequestBody DhOrderRejectBo bo) {
        return R.ok(dhOrderService.rejectOrder(bo));
    }
}
