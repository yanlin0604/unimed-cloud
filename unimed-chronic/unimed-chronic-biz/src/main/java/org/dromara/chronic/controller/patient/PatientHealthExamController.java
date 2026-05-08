package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.service.IChHealthExamService;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端体检检验
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端体检检验")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientHealthExamController {

    private final IChHealthExamService healthExamService;

    @Operation(summary = "查询体检检验列表")
    @GetMapping("/chronic/patient/health-exams")
    public R<List<ChHealthExamVo>> list() {
        Long patientId = LoginHelper.getUserId();
        return R.ok(healthExamService.queryByPatientId(patientId));
    }

    @Operation(summary = "体检检验详情")
    @GetMapping("/chronic/patient/health-exam/{examId}")
    public R<ChHealthExamVo> detail(@Parameter(description = "体检检验ID") @PathVariable Long examId) {
        return R.ok(healthExamService.queryById(examId));
    }
}
