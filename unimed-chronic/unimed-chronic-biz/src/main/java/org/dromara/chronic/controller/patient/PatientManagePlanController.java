package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChManagePlanVo;
import org.dromara.chronic.service.IChManagePlanService;
import org.dromara.common.core.domain.R;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端管理方案
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端管理方案")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientManagePlanController {

    private final IChManagePlanService managePlanService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询当前管理方案")
    @GetMapping("/chronic/patient/manage-plan/current")
    public R<ChManagePlanVo> currentPlan() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(managePlanService.queryCurrentPlan(patientId));
    }
}
