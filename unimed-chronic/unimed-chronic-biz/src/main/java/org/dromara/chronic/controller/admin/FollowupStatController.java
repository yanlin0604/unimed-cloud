package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChFollowupStatVo;
import org.dromara.chronic.service.IChFollowupStatService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 随访统计控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-随访统计")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/followup-stat")
public class FollowupStatController extends BaseController {

    private final IChFollowupStatService followupStatService;

    @Operation(summary = "获取随访统计全量看板数据")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/dashboard")
    public R<ChFollowupStatVo> dashboard(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                         @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getFullStatDashboard(areaCode, orgId));
    }

    @Operation(summary = "获取随访总览指标")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/overview")
    public R<ChFollowupStatVo.Overview> overview(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                 @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getOverview(areaCode, orgId));
    }

    @Operation(summary = "获取随访趋势统计")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/trend")
    public R<List<ChFollowupStatVo.TrendItem>> trend(@Parameter(description = "最近天数") @RequestParam(defaultValue = "15") int days,
                                                     @Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                     @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getTrend(days, areaCode, orgId));
    }

    @Operation(summary = "获取随访方式分布")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/type-distribution")
    public R<List<ChFollowupStatVo.TypeDistributionItem>> typeDistribution(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                           @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getTypeDistribution(areaCode, orgId));
    }

    @Operation(summary = "获取执行人工作量排行榜")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/assignee-ranking")
    public R<List<ChFollowupStatVo.AssigneeRankItem>> assigneeRanking(@Parameter(description = "榜单数量") @RequestParam(defaultValue = "10") int limit,
                                                                      @Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                      @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getAssigneeRanking(limit, areaCode, orgId));
    }

    @Operation(summary = "获取病种随访统计")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/disease-stats")
    public R<List<ChFollowupStatVo.DiseaseStatItem>> diseaseStats(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                  @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getDiseaseStats(areaCode, orgId));
    }

    @Operation(summary = "获取随访结论分布")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/result-distribution")
    public R<List<ChFollowupStatVo.ResultDistributionItem>> resultDistribution(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                                @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getResultDistribution(areaCode, orgId));
    }

    @Operation(summary = "获取康复评级分布")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/rehab-distribution")
    public R<List<ChFollowupStatVo.RehabDistributionItem>> rehabDistribution(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                             @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getRehabDistribution(areaCode, orgId));
    }

    @Operation(summary = "获取任务状态分布")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/status-distribution")
    public R<List<ChFollowupStatVo.StatusDistributionItem>> statusDistribution(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                               @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getStatusDistribution(areaCode, orgId));
    }

    @Operation(summary = "获取任务来源拆解")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/task-type-distribution")
    public R<List<ChFollowupStatVo.TaskTypeDistributionItem>> taskTypeDistribution(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                                    @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getTaskTypeDistribution(areaCode, orgId));
    }

    @Operation(summary = "获取失访/取消原因分布")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/lost-reason-stats")
    public R<List<ChFollowupStatVo.LostReasonItem>> lostReasonStats(@Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                    @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getLostReasonStats(areaCode, orgId));
    }

    @Operation(summary = "获取控制/逾期趋势统计")
    @SaCheckPermission("chronic:followup-stat:query")
    @GetMapping("/controlled-trend")
    public R<List<ChFollowupStatVo.RateTrendItem>> controlledTrend(@Parameter(description = "最近天数") @RequestParam(defaultValue = "15") int days,
                                                                   @Parameter(description = "区域编码") @RequestParam(required = false) String areaCode,
                                                                   @Parameter(description = "机构ID") @RequestParam(required = false) Long orgId) {
        return R.ok(followupStatService.getControlledTrend(days, areaCode, orgId));
    }
}
