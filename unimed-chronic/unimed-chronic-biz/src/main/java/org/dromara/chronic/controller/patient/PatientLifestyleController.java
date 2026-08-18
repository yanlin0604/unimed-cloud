package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChLifestyleRecordBo;
import org.dromara.chronic.domain.vo.ChLifestyleRecordVo;
import org.dromara.chronic.service.IChLifestyleRecordService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端生活方式
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端生活方式")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientLifestyleController {

    private final IChLifestyleRecordService lifestyleRecordService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "新增生活方式记录")
    @RepeatSubmit
    @PostMapping("/chronic/patient/lifestyle")
    public R<Long> add(@Validated @RequestBody ChLifestyleRecordBo bo) {
        // 患者身份取自登录上下文，禁止前端传入 patientId
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        return R.ok(lifestyleRecordService.add(bo));
    }

    @Operation(summary = "查询生活方式趋势")
    @GetMapping("/chronic/patient/lifestyle/trend")
    public R<List<ChLifestyleRecordVo>> trend(@Parameter(description = "查询数量，默认12") @RequestParam(required = false, defaultValue = "12") Integer limit) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(lifestyleRecordService.queryTrend(patientId, limit));
    }

    @Operation(summary = "查询最近生活方式记录")
    @GetMapping("/chronic/patient/lifestyle/latest")
    public R<ChLifestyleRecordVo> latest() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(lifestyleRecordService.queryLatest(patientId));
    }
}
