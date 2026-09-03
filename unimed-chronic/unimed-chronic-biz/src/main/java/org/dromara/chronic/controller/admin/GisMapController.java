package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.manager.GisMapManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 慢病管理全域 GIS 态势感知与空间分析
 *
 * @author unimed
 */
@Tag(name = "慢病管理-全域GIS感知")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/gis")
public class GisMapController extends BaseController {

    private final GisMapManager gisMapManager;

    /**
     * 获取全县慢病空间网格与机构态势全景
     */
    @Operation(summary = "获取全县慢病空间GIS概览")
    @SaCheckPermission("chronic:gis:query")
    @GetMapping("/county-overview")
    public R<Map<String, Object>> getCountyOverview(@Parameter(description = "区县编码(可选)") @RequestParam(required = false) String countyCode) {
        return R.ok(gisMapManager.getCountyGisOverview(countyCode));
    }
}
