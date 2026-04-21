package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端个人中心
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端个人中心")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/patient")
public class PatientCenterController extends BaseController {

    private final IChPatientProfileService patientProfileService;

    /**
     * 个人中心
     * 认证模块完成前临时使用 patientId 查询
     */
    @Operation(summary = "个人中心")
    @SaCheckPermission("chronic:patient:center")
    @GetMapping("/center")
    public R<ChPatientDetailVo> center(@Parameter(description = "患者ID") @RequestParam Long patientId) {
        return R.ok(patientProfileService.queryDetailById(patientId));
    }
}
