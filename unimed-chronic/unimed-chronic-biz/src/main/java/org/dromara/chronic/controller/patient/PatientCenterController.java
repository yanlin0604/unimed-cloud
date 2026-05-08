package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端个人中心
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端个人中心")
@SaCheckLogin
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/patient")
public class PatientCenterController {

    private final IChPatientProfileService patientProfileService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "个人中心")
    @GetMapping("/center")
    public R<ChPatientDetailVo> center() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(patientProfileService.queryDetailById(patientId));
    }
}
