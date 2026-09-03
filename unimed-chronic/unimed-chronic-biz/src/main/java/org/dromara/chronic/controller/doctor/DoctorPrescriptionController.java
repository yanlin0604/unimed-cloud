package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChPrescription;
import org.dromara.chronic.mapper.ChPrescriptionMapper;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 医生端处方管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端处方")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/patient")
public class DoctorPrescriptionController extends BaseController {

    private final ChPrescriptionMapper prescriptionMapper;
    private final DoctorScopeGuard doctorScopeGuard;

    @Operation(summary = "查询患者处方列表")
    @SaCheckPermission("chronic:doctor:patient:detail")
    @GetMapping("/{patientId}/prescription")
    public R<List<ChPrescription>> list(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId) {
        doctorScopeGuard.assertPatientOwned(patientId);
        List<ChPrescription> list = prescriptionMapper.selectList(
            Wrappers.<ChPrescription>lambdaQuery()
                .eq(ChPrescription::getPatientId, patientId)
                .orderByDesc(ChPrescription::getPrescriptionTime)
        );
        return R.ok(list);
    }

    @Operation(summary = "医生开具处方")
    @SaCheckPermission("chronic:doctor:patient:detail")
    @RepeatSubmit
    @PostMapping("/{patientId}/prescription")
    public R<Long> createPrescription(@PathVariable Long patientId, @RequestBody ChPrescription prescription) {
        doctorScopeGuard.assertPatientOwned(patientId);
        prescription.setPrescriptionId(IdUtil.getSnowflakeNextId());
        prescription.setPatientId(patientId);
        prescription.setDoctorUserId(LoginHelper.getUserId());
        if (StrUtil.isBlank(prescription.getPrescriptionNo())) {
            prescription.setPrescriptionNo("RX" + System.currentTimeMillis() + (int)(Math.random() * 1000));
        }
        if (prescription.getPrescriptionTime() == null) {
            prescription.setPrescriptionTime(LocalDateTime.now());
        }
        if (StrUtil.isBlank(prescription.getStatus())) {
            prescription.setStatus("ISSUED");
        }
        prescriptionMapper.insert(prescription);
        return R.ok(prescription.getPrescriptionId());
    }
}
