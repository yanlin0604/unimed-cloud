package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthEducationContentBo;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.manager.EducationPushManager;
import org.dromara.chronic.service.IChHealthEducationService;
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
 * 宣教内容管理+批量推送
 *
 * @author unimed
 */
@Tag(name = "慢病管理-宣教规则")
@Validated
@RestController
@RequiredArgsConstructor
public class EducationContentController {

    private final IChHealthEducationService healthEducationService;
    private final EducationPushManager educationPushManager;

    @Operation(summary = "新增宣教内容")
    @SaCheckPermission("chronic:education:add")
    @Log(title = "宣教内容", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/education-content")
    public R<Long> addContent(@Validated @RequestBody ChHealthEducationContentBo bo) {
        return R.ok(healthEducationService.createContent(bo));
    }

    @Operation(summary = "分页查询宣教内容")
    @SaCheckPermission("chronic:education:list")
    @GetMapping("/chronic/admin/education-content/page")
    public TableDataInfo<ChHealthEducationContentVo> contentPage(ChHealthEducationContentBo bo, PageQuery pageQuery) {
        return healthEducationService.queryContentPageList(bo, pageQuery);
    }

    @Operation(summary = "宣教内容详情")
    @SaCheckPermission("chronic:education:query")
    @GetMapping("/chronic/admin/education-content/{contentId}")
    public R<ChHealthEducationContentVo> contentDetail(@Parameter(description = "内容ID") @PathVariable Long contentId) {
        return R.ok(healthEducationService.queryContentById(contentId));
    }

    @Operation(summary = "批量推送宣教内容")
    @SaCheckPermission("chronic:education:push")
    @Log(title = "宣教批量推送", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/education/push-broadcast")
    public R<Void> pushBroadcast(@Parameter(description = "内容ID") @RequestParam Long contentId,
                                 @RequestBody List<Long> patientIds,
                                 @Parameter(description = "触发类型") @RequestParam(defaultValue = "MANUAL") String triggerType,
                                 @Parameter(description = "推送渠道") @RequestParam(defaultValue = "WECHAT") String pushChannel) {
        return R.ok(educationPushManager.broadcastToPatients(contentId, patientIds, triggerType, pushChannel));
    }
}
