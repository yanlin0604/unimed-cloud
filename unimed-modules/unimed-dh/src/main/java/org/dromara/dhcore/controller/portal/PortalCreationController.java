package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.vo.DhAvatarVo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.domain.vo.portal.PortalBackgroundVo;
import org.dromara.dhcore.domain.vo.portal.PortalTemplateVo;
import org.dromara.dhcore.service.IDhBackgroundService;
import org.dromara.dhcore.service.IPortalCreationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端创作资产控制器
 * <p>
 * 提供数字人形象、音色、素材、模板的查询和保存接口。
 * 支持系统预设资产和用户自定义资产的管理。
 *
 * @author unimed
 */
@Tag(name = "C端-创作资产")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/creation")
public class PortalCreationController extends BaseController {

    private final IPortalCreationService portalCreationService;
    private final IDhBackgroundService dhBackgroundService;

    // ==================== 形象管理 ====================

    /**
     * 获取可用数字人形象列表
     * <p>
     * 查询系统预设形象 + 当前用户上传的形象
     */
    @Operation(summary = "获取数字人形象列表")
    @SaCheckLogin
    @GetMapping("/avatars")
    public R<List<DhAvatarVo>> getAvatars() {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalCreationService.listAvatars(userId));
    }

    /**
     * 保存用户上传的形象
     *
     * @param ossId  OSS 文件 ID
     * @param name   形象名称（可选）
     * @param imageUrl 图片 URL（可选，通过 OSS 服务获取）
     */
    @Operation(summary = "保存形象")
    @SaCheckLogin
    @PostMapping("/avatars/save")
    public R<DhAvatarVo> saveAvatar(
        @RequestParam String ossId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String imageUrl) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalCreationService.saveAvatar(userId, ossId, name, imageUrl));
    }

    /**
     * 删除用户自定义形象
     *
     * @param avatarId 形象 ID
     */
    @Operation(summary = "删除形象")
    @SaCheckLogin
    @DeleteMapping("/avatars/{avatarId}")
    public R<Void> deleteAvatar(@PathVariable Long avatarId) {
        Long userId = LoginHelper.getUserId();
        portalCreationService.deleteAvatar(userId, avatarId);
        return R.ok();
    }

    // ==================== 音色管理 ====================

    /**
     * 获取可用音色列表
     * <p>
     * 查询系统预设音色 + 当前用户克隆的音色
     */
    @Operation(summary = "获取音色列表")
    @SaCheckLogin
    @GetMapping("/voices")
    public R<List<DhVoiceVo>> getVoices() {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalCreationService.listVoices(userId));
    }

    /**
     * 保存用户上传的音色
     *
     * @param ossId     OSS 文件 ID
     * @param name      音色名称（可选）
     * @param sampleUrl 音频 URL（可选，通过 OSS 服务获取）
     */
    @Operation(summary = "保存音色")
    @SaCheckLogin
    @PostMapping("/voices/save")
    public R<DhVoiceVo> saveVoice(
        @RequestParam String ossId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String sampleUrl) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalCreationService.saveVoice(userId, ossId, name, sampleUrl));
    }

    /**
     * 删除用户自定义音色
     *
     * @param voiceId 音色 ID
     */
    @Operation(summary = "删除音色")
    @SaCheckLogin
    @DeleteMapping("/voices/{voiceId}")
    public R<Void> deleteVoice(@PathVariable Long voiceId) {
        Long userId = LoginHelper.getUserId();
        portalCreationService.deleteVoice(userId, voiceId);
        return R.ok();
    }

    // ==================== 素材管理 ====================

    /**
     * 获取当前用户素材列表（含系统预设）
     *
     * @param materialType 素材类型过滤（VIDEO/IMAGE/AUDIO），不传时返回全部
     */
    @Operation(summary = "获取素材列表")
    @SaCheckLogin
    @GetMapping("/materials")
    public R<List<DhMaterialVo>> getMaterials(
        @RequestParam(required = false) String materialType) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalCreationService.listMaterials(userId, materialType));
    }

    /**
     * 保存用户上传的素材
     *
     * @param ossId         OSS 文件 ID
     * @param materialType  素材类型（VIDEO/IMAGE/AUDIO）
     * @param fileName      文件名（可选）
     * @param fileUrl       文件 URL（可选）
     */
    @Operation(summary = "保存素材")
    @SaCheckLogin
    @PostMapping("/materials/save")
    public R<DhMaterialVo> saveMaterial(
        @RequestParam String ossId,
        @RequestParam String materialType,
        @RequestParam(required = false) String fileName,
        @RequestParam(required = false) String fileUrl) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalCreationService.saveMaterial(userId, ossId, materialType, fileName, fileUrl));
    }

    /**
     * 删除素材
     *
     * @param materialId 素材 ID
     */
    @Operation(summary = "删除素材")
    @SaCheckLogin
    @DeleteMapping("/materials/{materialId}")
    public R<Void> deleteMaterial(@PathVariable Long materialId) {
        Long userId = LoginHelper.getUserId();
        portalCreationService.deleteMaterial(userId, materialId);
        return R.ok();
    }

    // ==================== 背景管理 ====================

    /**
     * 获取可用背景列表
     * <p>
     * 查询启用状态的系统背景，按 sort_order 升序排列
     */
    @Operation(summary = "获取背景列表")
    @SaCheckLogin
    @GetMapping("/backgrounds")
    public R<List<PortalBackgroundVo>> getBackgrounds() {
        return R.ok(dhBackgroundService.listEnabled());
    }

    // ==================== 模板管理 ====================

    /**
     * 获取公开模板列表
     * <p>
     * TODO: 待模板表完善后，查询启用状态的公开模板
     */
    @Operation(summary = "获取模板列表")
    @GetMapping("/templates")
    public R<List<PortalTemplateVo>> getTemplates() {
        return R.ok(portalCreationService.listTemplates());
    }
}
