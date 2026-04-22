package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.vo.ChDiseaseAnalysisVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.manager.DashboardManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "专病患者列表")
    @SaCheckPermission("chronic:dashboard:list")
    @GetMapping("/chronic/admin/patient/special-disease/page")
    public TableDataInfo<ChPatientProfileVo> specialDiseasePage(ChPatientDiseaseBo bo, PageQuery pageQuery) {
        return dashboardManager.querySpecialDiseasePatientPage(bo, pageQuery, bo.getDiseaseScope());
    }

    @Operation(summary = "合并症患者列表")
    @SaCheckPermission("chronic:dashboard:list")
    @GetMapping("/chronic/admin/patient/comorbidity/page")
    public TableDataInfo<ChPatientProfileVo> comorbidityPage(ChPatientDiseaseBo bo, PageQuery pageQuery) {
        return dashboardManager.queryComorbidityPatientPage(bo, pageQuery);
    }

    @Operation(summary = "单病种分析看板")
    @SaCheckPermission("chronic:dashboard:query")
    @GetMapping("/chronic/admin/dashboard/disease-analysis")
    public R<ChDiseaseAnalysisVo> diseaseAnalysis(@Parameter(description = "病种编码") @RequestParam String diseaseCode) {
        return R.ok(dashboardManager.queryDiseaseAnalysis(diseaseCode));
    }
}
