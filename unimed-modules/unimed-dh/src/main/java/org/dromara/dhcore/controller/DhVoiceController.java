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
import org.dromara.dhcore.domain.bo.DhVoiceBo;
import org.dromara.dhcore.domain.bo.DhVoiceQueryBo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.service.IDhVoiceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * B端声音管理控制器
 *
 * @author unimed
 */
@Tag(name = "B端-声音管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/voice")
public class DhVoiceController extends BaseController {

    private final IDhVoiceService dhVoiceService;

    /**
     * 分页查询系统音色列表
     */
    @Operation(summary = "查询音色列表")
    @SaCheckPermission("dh:voice:list")
    @GetMapping("/list")
    public TableDataInfo<DhVoiceVo> list(DhVoiceQueryBo bo, PageQuery pageQuery) {
        return dhVoiceService.querySystemVoicePage(bo, pageQuery);
    }

    /**
     * 新增系统音色
     */
    @Operation(summary = "新增音色")
    @SaCheckPermission("dh:voice:add")
    @PostMapping
    public R<DhVoiceVo> add(@Validated @RequestBody DhVoiceBo bo) {
        return R.ok(dhVoiceService.saveSystemVoice(bo));
    }

    /**
     * 修改系统音色
     */
    @Operation(summary = "修改音色")
    @SaCheckPermission("dh:voice:edit")
    @PutMapping
    public R<DhVoiceVo> edit(@Validated @RequestBody DhVoiceBo bo) {
        return R.ok(dhVoiceService.updateSystemVoice(bo));
    }

    /**
     * 删除系统音色
     *
     * @param voiceId 音色ID
     */
    @Operation(summary = "删除音色")
    @SaCheckPermission("dh:voice:remove")
    @DeleteMapping("/{voiceId}")
    public R<Boolean> remove(@NotNull @PathVariable Long voiceId) {
        return R.ok(dhVoiceService.deleteSystemVoice(voiceId));
    }

    /**
     * 切换音色状态
     */
    @Operation(summary = "切换音色状态")
    @SaCheckPermission("dh:voice:status")
    @PostMapping("/status")
    public R<DhVoiceVo> changeStatus(@Validated @RequestBody DhConfigStatusBo bo) {
        return R.ok(dhVoiceService.changeSystemVoiceStatus(bo));
    }
}
