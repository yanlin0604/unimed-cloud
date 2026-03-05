package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.*;
import org.dromara.dhcore.domain.vo.DhOrderDetailVo;
import org.dromara.dhcore.domain.vo.DhProductionAssetVo;
import org.dromara.dhcore.service.IDhOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 数字人口播生产控制器
 */
@Tag(name = "数字人口播-生产")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/production")
public class DhProductionController extends BaseController {

    private final IDhOrderService dhOrderService;

    @Operation(summary = "开始制作")
    @SaCheckPermission("dh:order:produce")
    @PostMapping("/{orderId}/start")
    public R<DhProductionAssetVo> start(@NotNull(message = "订单ID不能为空") @PathVariable Long orderId,
                                        @RequestBody(required = false) DhProductionStartBo bo) {
        return R.ok(dhOrderService.startProduction(orderId, bo));
    }

    @Operation(summary = "查询订单生产资产")
    @SaCheckPermission("dh:order:produce")
    @GetMapping("/{orderId}/asset")
    public R<DhProductionAssetVo> asset(@NotNull(message = "订单ID不能为空") @PathVariable Long orderId) {
        return R.ok(dhOrderService.getProductionAsset(orderId));
    }

    @Operation(summary = "保存生成元信息")
    @SaCheckPermission("dh:order:produce")
    @PostMapping("/{orderId}/meta")
    public R<DhProductionAssetVo> meta(@NotNull(message = "订单ID不能为空") @PathVariable Long orderId,
                                       @Validated @RequestBody DhProductionMetaBo bo) {
        return R.ok(dhOrderService.saveGenerationMeta(orderId, bo));
    }

    @Operation(summary = "上传成品视频元数据")
    @SaCheckPermission("dh:order:produce")
    @PostMapping("/{orderId}/video")
    public R<DhProductionAssetVo> video(@NotNull(message = "订单ID不能为空") @PathVariable Long orderId,
                                        @Validated @RequestBody DhProductionVideoBo bo) {
        return R.ok(dhOrderService.uploadResultVideo(orderId, bo));
    }

    @Operation(summary = "提交交付")
    @SaCheckPermission("dh:order:produce")
    @PostMapping("/submit")
    public R<DhOrderDetailVo> submit(@Validated @RequestBody DhProductionSubmitBo bo) {
        return R.ok(dhOrderService.submitOrderResult(bo));
    }
}
