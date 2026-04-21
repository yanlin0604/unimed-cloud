package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.manager.DashboardManager;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 大屏看板
 *
 * @author unimed
 */
@Tag(name = "慢病管理-看板")
@Validated
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardManager dashboardManager;

    @Operation(summary = "大屏看板数据")
    @SaCheckPermission("chronic:dashboard:query")
    @GetMapping("/chronic/admin/dashboard/big-screen")
    public R<Map<String, Object>> bigScreen(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode) {
        return R.ok(dashboardManager.bigScreenSummary(areaCode));
    }
}
