package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWarningRuleBo;
import org.dromara.chronic.domain.vo.ChWarningRuleVo;
import org.dromara.chronic.service.IChWarningRuleService;
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
 * 预警规则管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-预警规则")
@Validated
@RestController
@RequiredArgsConstructor
public class WarningRuleController {

    private final IChWarningRuleService warningRuleService;

    @Operation(summary = "新增预警规则")
    @SaCheckPermission("chronic:warning-rule:add")
    @Log(title = "预警规则", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/warning-rule")
    public R<Long> add(@Validated @RequestBody ChWarningRuleBo bo) {
        return R.ok(warningRuleService.add(bo));
    }

    @Operation(summary = "修改预警规则")
    @SaCheckPermission("chronic:warning-rule:edit")
    @Log(title = "预警规则", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/warning-rule/{ruleId}")
    public R<Void> edit(@Parameter(description = "规则ID") @PathVariable Long ruleId, @Validated @RequestBody ChWarningRuleBo bo) {
        bo.setRuleId(ruleId);
        return R.ok(warningRuleService.update(bo));
    }

    @Operation(summary = "分页查询预警规则")
    @SaCheckPermission("chronic:warning-rule:list")
    @GetMapping("/chronic/admin/warning-rule/page")
    public TableDataInfo<ChWarningRuleVo> page(ChWarningRuleBo bo, PageQuery pageQuery) {
        return warningRuleService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "预警规则详情")
    @SaCheckPermission("chronic:warning-rule:query")
    @GetMapping("/chronic/admin/warning-rule/{ruleId}")
    public R<ChWarningRuleVo> detail(@Parameter(description = "规则ID") @PathVariable Long ruleId) {
        return R.ok(warningRuleService.queryById(ruleId));
    }

    @Operation(summary = "按病种查询预警规则")
    @SaCheckPermission("chronic:warning-rule:query")
    @GetMapping("/chronic/admin/warning-rule/by-disease/{diseaseCode}")
    public R<List<ChWarningRuleVo>> listByDisease(@Parameter(description = "病种代码") @PathVariable String diseaseCode) {
        return R.ok(warningRuleService.queryByDiseaseCode(diseaseCode));
    }
}
