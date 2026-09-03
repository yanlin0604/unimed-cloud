package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientFeedback;
import org.dromara.chronic.mapper.ChPatientFeedbackMapper;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端意见反馈与问题上报
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端意见反馈")
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class PatientFeedbackController extends BaseController {

    private final ChPatientFeedbackMapper feedbackMapper;
    private final PatientContextHelper patientContextHelper;

    /**
     * 提交意见反馈
     */
    @Operation(summary = "提交意见反馈")
    @RepeatSubmit
    @PostMapping("/chronic/patient/feedback")
    public R<Long> submitFeedback(@RequestBody ChPatientFeedback feedback) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        if (StrUtil.isBlank(feedback.getContent())) {
            throw new ServiceException("反馈内容不能为空");
        }
        feedback.setId(IdUtil.getSnowflakeNextId());
        feedback.setPatientId(patientId);
        if (StrUtil.isBlank(feedback.getFeedbackType())) {
            feedback.setFeedbackType("SUGGESTION");
        }
        feedback.setReplyStatus("PENDING");
        feedbackMapper.insert(feedback);
        return R.ok(feedback.getId());
    }

    /**
     * 查看我的反馈记录与回复
     */
    @Operation(summary = "查询我的反馈记录")
    @GetMapping("/chronic/patient/feedback/history")
    public R<List<ChPatientFeedback>> myFeedback() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(feedbackMapper.selectList(
            Wrappers.<ChPatientFeedback>lambdaQuery()
                .eq(ChPatientFeedback::getPatientId, patientId)
                .orderByDesc(ChPatientFeedback::getCreateTime)
        ));
    }
}
