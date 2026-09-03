package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChAiCallTaskBo;
import org.dromara.chronic.domain.vo.ChAiCallTaskVo;
import org.dromara.chronic.manager.AiFollowupCallManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 智能随访语音外呼开放接口
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-开放接口-AI智能外呼")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiAiCallController extends BaseController {

    private final AiFollowupCallManager aiFollowupCallManager;

    @Operation(summary = "创建AI智能随访外呼任务")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/ai-call/task/create")
    public R<Long> createTask(@Validated @RequestBody ChAiCallTaskBo bo) {
        return R.ok(aiFollowupCallManager.createCallTask(bo));
    }

    @Operation(summary = "接收AI智能外呼通话完成回调")
    @PostMapping("/chronic/openapi/ai-call/callback")
    public R<Boolean> callCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("收到 AI 智能外呼第三方回调: {}", callbackData);
        Long taskId = callbackData.get("taskId") != null ? Long.valueOf(callbackData.get("taskId").toString()) : null;
        String status = (String) callbackData.get("status");
        String audioUrl = (String) callbackData.get("audioUrl");
        String transcript = (String) callbackData.get("transcript");
        String metricsJson = callbackData.get("metrics") != null ? callbackData.get("metrics").toString() : null;
        String feedback = (String) callbackData.get("feedback");

        Boolean success = aiFollowupCallManager.handleCallCallback(taskId, status, audioUrl, transcript, metricsJson, feedback);
        return R.ok(success);
    }

    @Operation(summary = "分页查询AI智能外呼任务")
    @GetMapping("/chronic/openapi/ai-call/tasks")
    public TableDataInfo<ChAiCallTaskVo> page(ChAiCallTaskBo bo, PageQuery pageQuery) {
        return aiFollowupCallManager.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "模拟执行AI智能外呼任务（仿真测试）")
    @PostMapping("/chronic/openapi/ai-call/simulate/{taskId}")
    public R<Boolean> simulateCall(@PathVariable Long taskId) {
        return R.ok(aiFollowupCallManager.simulateCall(taskId));
    }
}
