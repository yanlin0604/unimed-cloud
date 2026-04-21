package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupAnswerBo;
import org.dromara.chronic.domain.bo.ChFollowupQuestionnaireBo;
import org.dromara.chronic.domain.vo.ChFollowupAnswerVo;
import org.dromara.chronic.domain.vo.ChFollowupQuestionnaireVo;
import org.dromara.chronic.service.IChFollowupQuestionnaireService;
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
 * 随访问卷模板管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-随访问卷")
@Validated
@RestController
@RequiredArgsConstructor
public class FollowupQuestionnaireController {

    private final IChFollowupQuestionnaireService questionnaireService;

    @Operation(summary = "新增随访问卷")
    @SaCheckPermission("chronic:questionnaire:add")
    @Log(title = "随访问卷", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/followup-questionnaire")
    public R<Long> add(@Validated @RequestBody ChFollowupQuestionnaireBo bo) {
        return R.ok(questionnaireService.createQuestionnaire(bo));
    }

    @Operation(summary = "修改随访问卷")
    @SaCheckPermission("chronic:questionnaire:edit")
    @Log(title = "随访问卷", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-questionnaire/{questionnaireId}")
    public R<Void> edit(@PathVariable @Parameter(description = "问卷ID") Long questionnaireId, @Validated @RequestBody ChFollowupQuestionnaireBo bo) {
        bo.setQuestionnaireId(questionnaireId);
        return R.ok(questionnaireService.updateQuestionnaire(bo));
    }

    @Operation(summary = "分页查询随访问卷")
    @SaCheckPermission("chronic:questionnaire:list")
    @GetMapping("/chronic/admin/followup-questionnaire/page")
    public TableDataInfo<ChFollowupQuestionnaireVo> page(ChFollowupQuestionnaireBo bo, PageQuery pageQuery) {
        return questionnaireService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "随访问卷详情")
    @SaCheckPermission("chronic:questionnaire:query")
    @GetMapping("/chronic/admin/followup-questionnaire/{questionnaireId}")
    public R<ChFollowupQuestionnaireVo> detail(@PathVariable @Parameter(description = "问卷ID") Long questionnaireId) {
        return R.ok(questionnaireService.queryById(questionnaireId));
    }

    @Operation(summary = "切换问卷启用状态")
    @SaCheckPermission("chronic:questionnaire:status")
    @Log(title = "随访问卷状态", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-questionnaire/{questionnaireId}/active")
    public R<Void> toggleActive(
            @PathVariable @Parameter(description = "问卷ID") Long questionnaireId,
            @RequestParam @Parameter(description = "是否启用") Boolean isActive) {
        return R.ok(questionnaireService.toggleActive(questionnaireId, isActive));
    }

    @Operation(summary = "按病种查询问卷列表")
    @SaCheckPermission("chronic:questionnaire:query")
    @GetMapping("/chronic/admin/followup-questionnaire/by-disease/{diseaseCode}")
    public R<List<ChFollowupQuestionnaireVo>> listByDisease(
            @PathVariable @Parameter(description = "病种编码") String diseaseCode) {
        return R.ok(questionnaireService.queryByDiseaseCode(diseaseCode));
    }

    @Operation(summary = "提交问卷答案")
    @SaCheckPermission("chronic:questionnaire:answer")
    @Log(title = "问卷作答", businessType = BusinessType.INSERT)
    @PostMapping("/chronic/admin/followup-questionnaire/{questionnaireId}/record/{recordId}/answers")
    public R<Void> submitAnswers(
            @PathVariable @Parameter(description = "问卷ID") Long questionnaireId,
            @PathVariable @Parameter(description = "记录ID") Long recordId,
            @Validated @RequestBody List<ChFollowupAnswerBo> answers) {
        return R.ok(questionnaireService.submitAnswers(recordId, questionnaireId, answers));
    }

    @Operation(summary = "查询问卷答案")
    @SaCheckPermission("chronic:questionnaire:query")
    @GetMapping("/chronic/admin/followup-questionnaire/record/{recordId}/answers")
    public R<List<ChFollowupAnswerVo>> queryAnswers(
            @PathVariable @Parameter(description = "记录ID") Long recordId) {
        return R.ok(questionnaireService.queryAnswersByRecordId(recordId));
    }
}
