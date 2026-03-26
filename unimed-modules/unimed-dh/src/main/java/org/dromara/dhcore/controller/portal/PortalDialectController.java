package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhDialectRecordBo;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;
import org.dromara.dhcore.domain.vo.DhDialectRecordVo;
import org.dromara.dhcore.service.IDhDialectPromptService;
import org.dromara.dhcore.service.IDhDialectRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端方言采集控制器
 *
 * @author unimed
 */
@Tag(name = "C端-方言采集")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/dialect")
public class PortalDialectController extends BaseController {

    private final IDhDialectPromptService dialectPromptService;
    private final IDhDialectRecordService dialectRecordService;

    /**
     * 获取启用的提示文字列表
     */
    @Operation(summary = "获取启用的提示文字列表")
    @GetMapping("/prompts")
    public R<List<DhDialectPromptVo>> getPrompts() {
        return R.ok(dialectPromptService.listEnabled());
    }

    /**
     * 提交录音记录
     */
    @Operation(summary = "提交录音记录")
    @PostMapping("/record")
    public R<DhDialectRecordVo> submitRecord(@Validated @RequestBody DhDialectRecordBo bo) {
        Long userId = LoginHelper.getUserId();
        return R.ok(dialectRecordService.submit(userId, bo));
    }
}
