package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChAreaDictVo;
import org.dromara.chronic.domain.vo.ChKpiDefinitionVo;
import org.dromara.chronic.domain.vo.ChOrgAreaMappingVo;
import org.dromara.chronic.domain.vo.ChStatAreaDayVo;
import org.dromara.chronic.manager.DashboardManager;
import org.dromara.chronic.mapper.ChOrgAreaMappingMapper;
import org.dromara.common.core.domain.R;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 行政区划管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-行政区划")
@Validated
@RestController
@RequiredArgsConstructor
public class AreaController {

    private final DashboardManager dashboardManager;
    private final ChOrgAreaMappingMapper orgAreaMappingMapper;

    @Operation(summary = "获取行政区划树")
    @SaCheckPermission("chronic:area:query")
    @GetMapping("/chronic/admin/area/tree")
    public R<List<ChAreaDictVo>> areaTree(@Parameter(description = "父级区域编码") @RequestParam(required = false) String parentAreaCode) {
        return R.ok(dashboardManager.buildAreaTree(parentAreaCode));
    }

    @Operation(summary = "查询机构区域映射")
    @SaCheckPermission("chronic:area:query")
    @GetMapping("/chronic/admin/area/org-mapping")
    public R<List<ChOrgAreaMappingVo>> orgAreaMapping(@Parameter(description = "区域编码") @RequestParam String areaCode) {
        return R.ok(orgAreaMappingMapper.selectVoList(
            Wrappers.<org.dromara.chronic.domain.entity.ChOrgAreaMapping>lambdaQuery()
                .eq(org.dromara.chronic.domain.entity.ChOrgAreaMapping::getAreaCode, areaCode)
        ));
    }

    @Operation(summary = "查询区域统计数据")
    @SaCheckPermission("chronic:area:query")
    @GetMapping("/chronic/admin/area/stats")
    public R<List<ChStatAreaDayVo>> areaStats(
        @Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
        @Parameter(description = "统计日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date statDate) {
        return R.ok(dashboardManager.queryAreaStats(areaCode, statDate));
    }

    @Operation(summary = "查询KPI指标列表")
    @SaCheckPermission("chronic:kpi:list")
    @GetMapping("/chronic/admin/kpi/list")
    public R<List<ChKpiDefinitionVo>> kpiList() {
        return R.ok(dashboardManager.queryKpiList());
    }
}
