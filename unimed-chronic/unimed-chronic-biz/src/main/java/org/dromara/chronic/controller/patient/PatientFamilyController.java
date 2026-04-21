package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端家属代管接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端家属代管")
@Validated
@RestController
@RequiredArgsConstructor
public class PatientFamilyController {

    private final IChPatientAccountService patientAccountService;

    /**
     * 绑定家属代管
     */
    @Operation(summary = "绑定家属代管")
    @SaCheckLogin
    @PostMapping("/chronic/patient/family/bind")
    public R<Boolean> bindFamily(@Validated @RequestBody ChPatientAccountBo bo) {
        return R.ok(patientAccountService.bindFamilyProxy(bo));
    }

    /**
     * 家属代管列表
     */
    @Operation(summary = "家属代管列表")
    @SaCheckLogin
    @GetMapping("/chronic/patient/family/list/{masterAccountId}")
    public R<List<ChPatientAccountVo>> listFamily(@Parameter(description = "主账号ID") @PathVariable Long masterAccountId) {
        return R.ok(patientAccountService.queryFamilyProxies(masterAccountId));
    }

    /**
     * 解绑家属代管
     */
    @Operation(summary = "解绑家属代管")
    @SaCheckLogin
    @DeleteMapping("/chronic/patient/family/{accountId}")
    public R<Boolean> unbindFamily(@Parameter(description = "账号ID") @PathVariable Long accountId) {
        return R.ok(patientAccountService.unbindFamilyProxy(accountId));
    }
}
