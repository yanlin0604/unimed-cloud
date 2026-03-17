package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.vo.portal.PortalVoiceVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * C端声音克隆控制器
 * <p>
 * 提供声音克隆训练提交、训练进度查询、克隆音色列表等接口�?
 * 当前为骨架实现——声音克隆训练任务表尚未建立，接口返回占位数据�?
 * 待与 unimed-dh-relay 训练接口集成后补充完整逻辑�?
 *
 * @author unimed
 */
@Tag(name = "C�?声音克隆")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/voice-clone")
public class PortalVoiceCloneController extends BaseController {

    /**
     * 提交声音克隆训练
     * <p>
     * TODO: 集成 unimed-dh-relay 训练 API，创建训练任务记�?
     */
    @Operation(summary = "提交声音克隆训练")
    @PostMapping("/train")
    public R<VoiceCloneTaskVo> submitTrain(@Validated @RequestBody VoiceCloneTrainBo bo) {
        Long userId = LoginHelper.getUserId();
        // 骨架：训练任务表未建立，返回占位响应
        VoiceCloneTaskVo taskVo = new VoiceCloneTaskVo();
        taskVo.setTaskId("placeholder-" + System.currentTimeMillis());
        taskVo.setStatus("PENDING");
        taskVo.setMessage("声音克隆训练功能即将上线");
        return R.ok(taskVo);
    }

    /**
     * 查询训练进度
     * <p>
     * TODO: 从训练任务表�?relay 服务查询实际进度
     */
    @Operation(summary = "查询训练进度")
    @GetMapping("/progress/{taskId}")
    public R<VoiceCloneTaskVo> getProgress(@PathVariable String taskId) {
        // 骨架：返回占位进�?
        VoiceCloneTaskVo taskVo = new VoiceCloneTaskVo();
        taskVo.setTaskId(taskId);
        taskVo.setStatus("PENDING");
        taskVo.setProgress(0);
        taskVo.setMessage("声音克隆训练功能即将上线");
        return R.ok(taskVo);
    }

    /**
     * 获取当前用户克隆音色列表
     * <p>
     * TODO: 待声音克隆完成记录表建立后，查询用户的克隆音�?
     */
    @Operation(summary = "获取克隆音色列表")
    @GetMapping("/list")
    public R<List<PortalVoiceVo>> getClonedVoices() {
        Long userId = LoginHelper.getUserId();
        // 骨架：克隆音色表未建立，返回空列�?
        return R.ok(Collections.emptyList());
    }

    // ==================== 内嵌 BO/VO ====================

    /**
     * 声音克隆训练请求对象
     */
    @Data
    public static class VoiceCloneTrainBo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /** 音源视频/音频URL */
        @NotBlank(message = "音源地址不能为空")
        private String sourceUrl;

        /** 音色名称 */
        @NotBlank(message = "音色名称不能为空")
        private String voiceName;

        /** 备注 */
        private String remark;
    }

    /**
     * 声音克隆任务视图对象
     */
    @Data
    public static class VoiceCloneTaskVo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /** 任务ID */
        private String taskId;

        /** 任务状态（PENDING/TRAINING/COMPLETED/FAILED�?*/
        private String status;

        /** 训练进度百分比（0-100�?*/
        private Integer progress;

        /** 状态说�?*/
        private String message;
    }
}
