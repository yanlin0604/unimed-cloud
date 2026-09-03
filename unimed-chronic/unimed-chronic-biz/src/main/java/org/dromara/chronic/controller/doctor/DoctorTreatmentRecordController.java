package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChTreatmentRecord;
import org.dromara.chronic.mapper.ChTreatmentRecordMapper;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端非药物治疗管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端治疗记录")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/patient")
public class DoctorTreatmentRecordController extends BaseController {

    private final ChTreatmentRecordMapper treatmentRecordMapper;
    private final DoctorScopeGuard doctorScopeGuard;

    @Operation(summary = "查询患者非药物治疗记录")
    @SaCheckPermission("chronic:doctor:patient:detail")
    @GetMapping("/{patientId}/treatment")
    public R<List<ChTreatmentRecord>> list(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId) {
        doctorScopeGuard.assertPatientOwned(patientId);
        List<ChTreatmentRecord> list = treatmentRecordMapper.selectList(
            Wrappers.<ChTreatmentRecord>lambdaQuery()
                .eq(ChTreatmentRecord::getPatientId, patientId)
                .orderByDesc(ChTreatmentRecord::getStartDate)
        );
        return R.ok(list);
    }

    @Operation(summary = "医生录入/开具治疗康复记录")
    @SaCheckPermission("chronic:doctor:patient:detail")
    @RepeatSubmit
    @PostMapping("/{patientId}/treatment")
    public R<Long> createTreatment(@PathVariable Long patientId, @RequestBody ChTreatmentRecord record) {
        doctorScopeGuard.assertPatientOwned(patientId);
        record.setTreatmentId(IdUtil.getSnowflakeNextId());
        record.setPatientId(patientId);
        if (StrUtil.isBlank(record.getStatus())) {
            record.setStatus("ACTIVE");
        }
        if (StrUtil.isBlank(record.getOperatorDoctorName())) {
            record.setOperatorDoctorName(LoginHelper.getUsername());
        }
        treatmentRecordMapper.insert(record);
        return R.ok(record.getTreatmentId());
    }
}
