package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChNotificationTemplateBo;
import org.dromara.chronic.domain.vo.ChNotificationTemplateVo;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知模板管理后台
 * <p>
 * 维护随访提醒 / SOS 通知等文案模板，模板内容支持 {name} 形式占位符，
 * 由 {@link IChNotificationTemplateService#render} 渲染。
 *
 * @author unimed
 */
@Tag(name = "慢病管理-通知模板")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/notification-template")
public class NotificationTemplateController extends BaseController {

    private final IChNotificationTemplateService notificationTemplateService;

    @Operation(summary = "分页查询通知模板")
    @SaCheckPermission("chronic:notification-template:list")
    @GetMapping("/page")
    public TableDataInfo<ChNotificationTemplateVo> page(ChNotificationTemplateBo bo, PageQuery pageQuery) {
        return notificationTemplateService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "通知模板详情")
    @SaCheckPermission("chronic:notification-template:query")
    @GetMapping("/{templateId}")
    public R<ChNotificationTemplateVo> detail(@Parameter(description = "模板ID") @PathVariable Long templateId) {
        return R.ok(notificationTemplateService.queryById(templateId));
    }

    @Operation(summary = "新增通知模板")
    @SaCheckPermission("chronic:notification-template:add")
    @Log(title = "通知模板", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ChNotificationTemplateBo bo) {
        return notificationTemplateService.insertByBo(bo) ? R.ok() : R.fail();
    }

    @Operation(summary = "修改通知模板")
    @SaCheckPermission("chronic:notification-template:edit")
    @Log(title = "通知模板", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ChNotificationTemplateBo bo) {
        return notificationTemplateService.updateByBo(bo) ? R.ok() : R.fail();
    }

    @Operation(summary = "删除通知模板")
    @SaCheckPermission("chronic:notification-template:remove")
    @Log(title = "通知模板", businessType = BusinessType.DELETE)
    @RepeatSubmit
    @DeleteMapping("/{templateId}")
    public R<Void> remove(@Parameter(description = "模板ID") @PathVariable Long templateId) {
        return notificationTemplateService.deleteById(templateId) ? R.ok() : R.fail();
    }

    @Operation(summary = "启用/停用通知模板")
    @SaCheckPermission("chronic:notification-template:status")
    @Log(title = "通知模板", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{templateId}/status")
    public R<Void> changeStatus(@Parameter(description = "模板ID") @PathVariable Long templateId,
                                @Parameter(description = "是否启用 1启用 0停用") @RequestParam String isActive) {
        return R.ok(notificationTemplateService.updateStatus(templateId, isActive));
    }
}
