package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupRuleBo;
import org.dromara.chronic.domain.vo.ChFollowupRuleVo;
import org.dromara.chronic.service.IChFollowupRuleService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 慢病随访排期规则配置控制器
 * <p>
 * 平台级配置(仅 Admin),将随访排期规则从代码 switch 迁移为运营可配置。
 * 引擎"查表优先、代码内置兜底",未配置时行为与旧实现一致。
 *
 * @author unimed
 */
@Tag(name = "慢病管理-随访排期规则")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/followup-rule")
public class FollowupRuleController extends BaseController {

    private final IChFollowupRuleService followupRuleService;

    @Operation(summary = "分页查询随访排期规则")
    @SaCheckPermission("chronic:followupRule:list")
    @GetMapping("/page")
    public TableDataInfo<ChFollowupRuleVo> page(ChFollowupRuleBo bo, PageQuery pageQuery) {
        return followupRuleService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "随访排期规则详情")
    @SaCheckPermission("chronic:followupRule:list")
    @GetMapping("/{id}")
    public R<ChFollowupRuleVo> detail(@PathVariable @Parameter(description = "规则ID") Long id) {
        return R.ok(followupRuleService.queryById(id));
    }

    @Operation(summary = "新增随访排期规则")
    @SaCheckPermission("chronic:followupRule:add")
    @Log(title = "随访排期规则", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Long> add(@Validated @RequestBody ChFollowupRuleBo bo) {
        return R.ok(followupRuleService.createRule(bo));
    }

    @Operation(summary = "修改随访排期规则")
    @SaCheckPermission("chronic:followupRule:edit")
    @Log(title = "随访排期规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{id}")
    public R<Void> edit(@PathVariable @Parameter(description = "规则ID") Long id,
                        @Validated @RequestBody ChFollowupRuleBo bo) {
        bo.setId(id);
        return R.ok(followupRuleService.updateRule(bo));
    }

    @Operation(summary = "切换规则启用状态")
    @SaCheckPermission("chronic:followupRule:status")
    @Log(title = "随访排期规则状态", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public R<Void> toggleActive(@PathVariable @Parameter(description = "规则ID") Long id,
                                @RequestParam @Parameter(description = "是否启用") Boolean isActive) {
        return R.ok(followupRuleService.toggleActive(id, isActive));
    }

    @Operation(summary = "批量删除随访排期规则")
    @SaCheckPermission("chronic:followupRule:remove")
    @Log(title = "随访排期规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable @NotEmpty(message = "规则ID集合不能为空")
                          @Parameter(description = "规则ID集合") Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return R.ok(followupRuleService.deleteRules(idList));
    }
}