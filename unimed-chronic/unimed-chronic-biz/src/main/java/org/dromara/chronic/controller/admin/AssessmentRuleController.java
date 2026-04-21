package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChAssessmentRuleBo;
import org.dromara.chronic.domain.vo.ChAssessmentRuleVo;
import org.dromara.chronic.service.IChRiskAssessmentService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 风险评估规则控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-风险评估规则")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/assessment-rule")
public class AssessmentRuleController {

    private final IChRiskAssessmentService riskAssessmentService;

    @Operation(summary = "分页查询评估规则")
    @SaCheckPermission("chronic:assessment-rule:list")
    @GetMapping("/page")
    public TableDataInfo<ChAssessmentRuleVo> page(ChAssessmentRuleBo bo, PageQuery pageQuery) {
        return riskAssessmentService.queryRulePage(bo, pageQuery);
    }

    @Operation(summary = "新增评估规则")
    @SaCheckPermission("chronic:assessment-rule:add")
    @Log(title = "风险评估规则", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody ChAssessmentRuleBo bo) {
        return riskAssessmentService.createRule(bo) ? R.ok() : R.fail();
    }
}
