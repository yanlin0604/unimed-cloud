package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhMemberConfigBo;
import org.dromara.dhcore.domain.bo.DhMemberConfigQueryBo;
import org.dromara.dhcore.domain.bo.DhNotifyTemplateBo;
import org.dromara.dhcore.domain.bo.DhNotifyTemplateQueryBo;
import org.dromara.dhcore.domain.bo.DhPaymentPriceConfigBo;
import org.dromara.dhcore.domain.bo.DhPaymentPriceQueryBo;
import org.dromara.dhcore.domain.bo.DhQrUploadConfigBo;
import org.dromara.dhcore.domain.bo.DhQrUploadConfigQueryBo;
import org.dromara.dhcore.domain.bo.DhSensitiveWordBo;
import org.dromara.dhcore.domain.bo.DhSensitiveWordImportBo;
import org.dromara.dhcore.domain.bo.DhSensitiveWordQueryBo;
import org.dromara.dhcore.domain.bo.DhVideoUploadConfigBo;
import org.dromara.dhcore.domain.bo.DhVideoUploadConfigQueryBo;
import org.dromara.dhcore.domain.vo.DhMemberConfigVo;
import org.dromara.dhcore.domain.vo.DhNotifyTemplateVo;
import org.dromara.dhcore.domain.vo.DhPaymentPriceConfigVo;
import org.dromara.dhcore.domain.vo.DhQrUploadConfigVo;
import org.dromara.dhcore.domain.vo.DhSensitiveWordVo;
import org.dromara.dhcore.domain.vo.DhVideoUploadConfigVo;
import org.dromara.dhcore.service.IDhConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数字人口播配置中心控制器
 */
@Tag(name = "数字人口播-配置中心")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/config")
public class DhConfigController extends BaseController {

    private final IDhConfigService dhConfigService;

    @Operation(summary = "分页查询会员等级配置")
    @SaCheckPermission("dh:config:member:list")
    @GetMapping("/member/list")
    public TableDataInfo<DhMemberConfigVo> memberList(DhMemberConfigQueryBo bo, PageQuery pageQuery) {
        return dhConfigService.queryMemberConfigPage(bo, pageQuery);
    }

    @Operation(summary = "新增会员等级配置")
    @SaCheckPermission("dh:config:member:add")
    @PostMapping("/member")
    public R<DhMemberConfigVo> addMember(@Validated @RequestBody DhMemberConfigBo bo) {
        return R.ok(dhConfigService.saveMemberConfig(bo));
    }

    @Operation(summary = "修改会员等级配置")
    @SaCheckPermission("dh:config:member:edit")
    @PutMapping("/member")
    public R<DhMemberConfigVo> editMember(@Validated @RequestBody DhMemberConfigBo bo) {
        return R.ok(dhConfigService.saveMemberConfig(bo));
    }

    @Operation(summary = "删除会员等级配置")
    @SaCheckPermission("dh:config:member:remove")
    @DeleteMapping("/member/{id}")
    public R<Boolean> removeMember(@NotNull(message = "配置ID不能为空") @PathVariable Long id) {
        return R.ok(dhConfigService.deleteMemberConfig(id));
    }

    @Operation(summary = "切换会员等级配置状态")
    @SaCheckPermission("dh:config:member:status")
    @PostMapping("/member/status")
    public R<DhMemberConfigVo> memberStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhConfigService.changeMemberConfigStatus(bo));
    }

    @Operation(summary = "分页查询充值档位配置")
    @SaCheckPermission("dh:config:price:list")
    @GetMapping("/price/list")
    public TableDataInfo<DhPaymentPriceConfigVo> priceList(DhPaymentPriceQueryBo bo, PageQuery pageQuery) {
        return dhConfigService.queryPaymentPricePage(bo, pageQuery);
    }

    @Operation(summary = "新增充值档位配置")
    @SaCheckPermission("dh:config:price:add")
    @PostMapping("/price")
    public R<DhPaymentPriceConfigVo> addPrice(@Validated @RequestBody DhPaymentPriceConfigBo bo) {
        return R.ok(dhConfigService.savePaymentPriceConfig(bo));
    }

    @Operation(summary = "修改充值档位配置")
    @SaCheckPermission("dh:config:price:edit")
    @PutMapping("/price")
    public R<DhPaymentPriceConfigVo> editPrice(@Validated @RequestBody DhPaymentPriceConfigBo bo) {
        return R.ok(dhConfigService.savePaymentPriceConfig(bo));
    }

    @Operation(summary = "删除充值档位配置")
    @SaCheckPermission("dh:config:price:remove")
    @DeleteMapping("/price/{id}")
    public R<Boolean> removePrice(@NotNull(message = "配置ID不能为空") @PathVariable Long id) {
        return R.ok(dhConfigService.deletePaymentPriceConfig(id));
    }

    @Operation(summary = "切换充值档位配置状态")
    @SaCheckPermission("dh:config:price:status")
    @PostMapping("/price/status")
    public R<DhPaymentPriceConfigVo> priceStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhConfigService.changePaymentPriceConfigStatus(bo));
    }

    @Operation(summary = "分页查询视频上传配置")
    @SaCheckPermission("dh:config:video:list")
    @GetMapping("/video/list")
    public TableDataInfo<DhVideoUploadConfigVo> videoList(DhVideoUploadConfigQueryBo bo, PageQuery pageQuery) {
        return dhConfigService.queryVideoUploadConfigPage(bo, pageQuery);
    }

    @Operation(summary = "新增视频上传配置")
    @SaCheckPermission("dh:config:video:add")
    @PostMapping("/video")
    public R<DhVideoUploadConfigVo> addVideo(@Validated @RequestBody DhVideoUploadConfigBo bo) {
        return R.ok(dhConfigService.saveVideoUploadConfig(bo));
    }

    @Operation(summary = "修改视频上传配置")
    @SaCheckPermission("dh:config:video:edit")
    @PutMapping("/video")
    public R<DhVideoUploadConfigVo> editVideo(@Validated @RequestBody DhVideoUploadConfigBo bo) {
        return R.ok(dhConfigService.saveVideoUploadConfig(bo));
    }

    @Operation(summary = "删除视频上传配置")
    @SaCheckPermission("dh:config:video:remove")
    @DeleteMapping("/video/{id}")
    public R<Boolean> removeVideo(@NotNull(message = "配置ID不能为空") @PathVariable Long id) {
        return R.ok(dhConfigService.deleteVideoUploadConfig(id));
    }

    @Operation(summary = "切换视频上传配置状态")
    @SaCheckPermission("dh:config:video:status")
    @PostMapping("/video/status")
    public R<DhVideoUploadConfigVo> videoStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhConfigService.changeVideoUploadConfigStatus(bo));
    }

    @Operation(summary = "分页查询二维码上传配置")
    @SaCheckPermission("dh:config:qr:list")
    @GetMapping("/qr/list")
    public TableDataInfo<DhQrUploadConfigVo> qrList(DhQrUploadConfigQueryBo bo, PageQuery pageQuery) {
        return dhConfigService.queryQrUploadConfigPage(bo, pageQuery);
    }

    @Operation(summary = "新增二维码上传配置")
    @SaCheckPermission("dh:config:qr:add")
    @PostMapping("/qr")
    public R<DhQrUploadConfigVo> addQr(@Validated @RequestBody DhQrUploadConfigBo bo) {
        return R.ok(dhConfigService.saveQrUploadConfig(bo));
    }

    @Operation(summary = "修改二维码上传配置")
    @SaCheckPermission("dh:config:qr:edit")
    @PutMapping("/qr")
    public R<DhQrUploadConfigVo> editQr(@Validated @RequestBody DhQrUploadConfigBo bo) {
        return R.ok(dhConfigService.saveQrUploadConfig(bo));
    }

    @Operation(summary = "删除二维码上传配置")
    @SaCheckPermission("dh:config:qr:remove")
    @DeleteMapping("/qr/{id}")
    public R<Boolean> removeQr(@NotNull(message = "配置ID不能为空") @PathVariable Long id) {
        return R.ok(dhConfigService.deleteQrUploadConfig(id));
    }

    @Operation(summary = "切换二维码上传配置状态")
    @SaCheckPermission("dh:config:qr:status")
    @PostMapping("/qr/status")
    public R<DhQrUploadConfigVo> qrStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhConfigService.changeQrUploadConfigStatus(bo));
    }

    @Operation(summary = "分页查询敏感词")
    @SaCheckPermission("dh:config:sensitive:list")
    @GetMapping("/sensitive/list")
    public TableDataInfo<DhSensitiveWordVo> sensitiveList(DhSensitiveWordQueryBo bo, PageQuery pageQuery) {
        return dhConfigService.querySensitiveWordPage(bo, pageQuery);
    }

    @Operation(summary = "新增敏感词")
    @SaCheckPermission("dh:config:sensitive:add")
    @PostMapping("/sensitive")
    public R<DhSensitiveWordVo> addSensitive(@Validated @RequestBody DhSensitiveWordBo bo) {
        return R.ok(dhConfigService.saveSensitiveWord(bo));
    }

    @Operation(summary = "修改敏感词")
    @SaCheckPermission("dh:config:sensitive:edit")
    @PutMapping("/sensitive")
    public R<DhSensitiveWordVo> editSensitive(@Validated @RequestBody DhSensitiveWordBo bo) {
        return R.ok(dhConfigService.saveSensitiveWord(bo));
    }

    @Operation(summary = "删除敏感词")
    @SaCheckPermission("dh:config:sensitive:remove")
    @DeleteMapping("/sensitive/{id}")
    public R<Boolean> removeSensitive(@NotNull(message = "敏感词ID不能为空") @PathVariable Long id) {
        return R.ok(dhConfigService.deleteSensitiveWord(id));
    }

    @Operation(summary = "切换敏感词状态")
    @SaCheckPermission("dh:config:sensitive:status")
    @PostMapping("/sensitive/status")
    public R<DhSensitiveWordVo> sensitiveStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhConfigService.changeSensitiveWordStatus(bo));
    }

    @Operation(summary = "批量导入敏感词")
    @SaCheckPermission("dh:config:sensitive:import")
    @PostMapping("/sensitive/import")
    public R<Map<String, Integer>> importSensitive(@Validated @RequestBody DhSensitiveWordImportBo bo) {
        return R.ok(dhConfigService.batchImportSensitiveWords(bo));
    }

    @Operation(summary = "分页查询通知模板")
    @SaCheckPermission("dh:config:notify:list")
    @GetMapping("/notify/list")
    public TableDataInfo<DhNotifyTemplateVo> notifyList(DhNotifyTemplateQueryBo bo, PageQuery pageQuery) {
        return dhConfigService.queryNotifyTemplatePage(bo, pageQuery);
    }

    @Operation(summary = "新增通知模板")
    @SaCheckPermission("dh:config:notify:add")
    @PostMapping("/notify")
    public R<DhNotifyTemplateVo> addNotify(@Validated @RequestBody DhNotifyTemplateBo bo) {
        return R.ok(dhConfigService.saveNotifyTemplate(bo));
    }

    @Operation(summary = "修改通知模板")
    @SaCheckPermission("dh:config:notify:edit")
    @PutMapping("/notify")
    public R<DhNotifyTemplateVo> editNotify(@Validated @RequestBody DhNotifyTemplateBo bo) {
        return R.ok(dhConfigService.saveNotifyTemplate(bo));
    }

    @Operation(summary = "切换通知模板状态")
    @SaCheckPermission("dh:config:notify:status")
    @PostMapping("/notify/status")
    public R<DhNotifyTemplateVo> notifyStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhConfigService.changeNotifyTemplateStatus(bo));
    }
}
