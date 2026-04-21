package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端签约管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端签约")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/patient")
public class DoctorContractController {

    private final IChPatientContractService patientContractService;

    @Operation(summary = "患者签约")
    @SaCheckPermission("chronic:doctor:patient:sign")
    @Log(title = "患者签约", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/{patientId}/sign")
    public R<Long> sign(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId, @Validated @RequestBody ChPatientContractBo bo) {
        bo.setPatientId(patientId);
        return R.ok(patientContractService.signContract(bo));
    }
}
