package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.vo.portal.PortalAvatarVo;
import org.dromara.dhcore.domain.vo.portal.PortalMaterialVo;
import org.dromara.dhcore.domain.vo.portal.PortalTemplateVo;
import org.dromara.dhcore.domain.vo.portal.PortalVoiceVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * C端创作资产控制器
 * <p>
 * 提供数字人形象、音色、素材、模板的查询接口。
 * 当前为骨架实现——Avatar、Voice、Material 实体尚未在后端建立，
 * 接口返回空列表作为占位，待实体和表结构完善后补充实际查询逻辑。
 * Template 复用现有 DhVideoUploadConfig（如有）或返回空列表。
 *
 * @author AI
 */
@Tag(name = "C端-创作资产")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/creation")
public class PortalCreationController extends BaseController {

    /**
     * 获取可用数字人形象列表
     * <p>
     * TODO: 待 dh_avatar 表和实体创建后，查询 系统预设 + 用户上传 的形象
     */
    @Operation(summary = "获取数字人形象列表")
    @SaCheckLogin
    @GetMapping("/avatars")
    public R<List<PortalAvatarVo>> getAvatars() {
        // 骨架：Avatar 实体未建立，返回空列表
        return R.ok(Collections.emptyList());
    }

    /**
     * 获取可用音色列表
     * <p>
     * TODO: 待 dh_voice 表和实体创建后，查询 系统预设 + 用户克隆 的音色
     */
    @Operation(summary = "获取音色列表")
    @SaCheckLogin
    @GetMapping("/voices")
    public R<List<PortalVoiceVo>> getVoices() {
        // 骨架：Voice 实体未建立，返回空列表
        return R.ok(Collections.emptyList());
    }

    /**
     * 获取当前用户素材列表
     * <p>
     * TODO: 待 dh_user_material 表和实体创建后，查询用户上传的素材
     */
    @Operation(summary = "获取素材列表")
    @SaCheckLogin
    @GetMapping("/materials")
    public R<List<PortalMaterialVo>> getMaterials() {
        Long userId = LoginHelper.getUserId();
        // 骨架：Material 实体未建立，返回空列表
        return R.ok(Collections.emptyList());
    }

    /**
     * 获取公开模板列表
     * <p>
     * TODO: 待模板表完善后，查询启用状态的公开模板
     */
    @Operation(summary = "获取模板列表")
    @GetMapping("/templates")
    public R<List<PortalTemplateVo>> getTemplates() {
        // 骨架：Template 查询逻辑待完善
        return R.ok(Collections.emptyList());
    }
}
