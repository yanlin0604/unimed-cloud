package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.vo.DhDashboardMetricsVo;
import org.dromara.dhcore.service.IDhDashboardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人口播看板控制器
 */
@Tag(name = "数字人口�?看板")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/dashboard")
public class DhDashboardController extends BaseController {

    private final IDhDashboardService dhDashboardService;

    @Operation(summary = "查询看板指标")
    @SaCheckPermission("dh:dashboard:list")
    @GetMapping("/metrics")
    public R<DhDashboardMetricsVo> metrics(@RequestParam("range") String range) {
        return R.ok(dhDashboardService.queryMetrics(range));
    }
}
