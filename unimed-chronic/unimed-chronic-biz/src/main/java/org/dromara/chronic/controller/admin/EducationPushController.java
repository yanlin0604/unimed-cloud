package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChEducationRuleBo;
import org.dromara.chronic.domain.vo.ChEducationRuleVo;
import org.dromara.chronic.service.IChEducationRuleService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宣教规则管理后台
 *
 * @author unimed
 */
@Validated
@Tag(name = "慢病管理-宣教推送")
@RestController
@RequiredArgsConstructor
public class EducationPushController extends BaseController {

    private final IChEducationRuleService educationRuleService;

    @Operation(summary = "新增宣教规则")
    @SaCheckPermission("chronic:education-rule:add")
    @Log(title = "宣教规则", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/education-rule")
    public R<Long> add(@Validated @RequestBody ChEducationRuleBo bo) {
        return R.ok(educationRuleService.add(bo));
    }

    @Operation(summary = "修改宣教规则")
    @SaCheckPermission("chronic:education-rule:edit")
    @Log(title = "宣教规则", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/education-rule/{ruleId}")
    public R<Void> edit(@Parameter(description = "规则ID") @PathVariable Long ruleId, @Validated @RequestBody ChEducationRuleBo bo) {
        bo.setRuleId(ruleId);
        return R.ok(educationRuleService.update(bo));
    }

    @Operation(summary = "分页查询宣教规则")
    @SaCheckPermission("chronic:education-rule:list")
    @GetMapping("/chronic/admin/education-rule/page")
    public TableDataInfo<ChEducationRuleVo> page(ChEducationRuleBo bo, PageQuery pageQuery) {
        return educationRuleService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "宣教规则详情")
    @SaCheckPermission("chronic:education-rule:query")
    @GetMapping("/chronic/admin/education-rule/{ruleId}")
    public R<ChEducationRuleVo> detail(@Parameter(description = "规则ID") @PathVariable Long ruleId) {
        return R.ok(educationRuleService.queryById(ruleId));
    }

    @Operation(summary = "查询启用中的宣教规则")
    @SaCheckPermission("chronic:education-rule:list")
    @GetMapping("/chronic/admin/education-rule/active")
    public R<List<ChEducationRuleVo>> activeRules() {
        return R.ok(educationRuleService.queryActiveRules());
    }
}
