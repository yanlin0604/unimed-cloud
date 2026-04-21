package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChExternalSyncLog;
import org.dromara.chronic.manager.PatientProfileManager;
import org.dromara.chronic.manager.RiskAssessmentManager;
import org.dromara.chronic.mapper.ChExternalSyncLogMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * HIS 同步开放接口
 * <p>
 * 患者同步、确诊结果同步(触发候选入组→建档→方案草案)
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-HIS同步")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiHisController {

    private final PatientProfileManager patientProfileManager;
    private final RiskAssessmentManager riskAssessmentManager;
    private final ChExternalSyncLogMapper externalSyncLogMapper;

    /**
     * HIS 患者信息同步（幂等：id_card 已存在则更新）
     */
    @Operation(summary = "HIS患者信息同步")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/his/patient/sync")
    public R<Long> patientSync(@Validated @RequestBody ChPatientProfileBo bo) {
        logSync("HIS_PATIENT", "INBOUND", "HIS", "SUCCESS",
            "同步患者: " + bo.getName() + ", 身份证: " + bo.getIdCard());
        Long patientId = patientProfileManager.createArchive(bo, java.util.Collections.emptyList(), java.util.Collections.emptyList());
        return R.ok(patientId);
    }

    /**
     * HIS 确诊结果同步
     * <p>
     * 触发流程：候选入组 → 建档 → 风险评估 → 方案草案
     * 异步处理，避免阻塞 HIS
     */
    @Operation(summary = "HIS确诊结果同步")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/his/diagnosis/confirm")
    public R<Long> diagnosisConfirm(
            @Parameter(description = "患者ID") @RequestParam Long patientId,
            @Parameter(description = "病种编码") @RequestParam String diseaseCode,
            @Parameter(description = "ICD编码") @RequestParam String icdCode,
            @Parameter(description = "诊断依据") @RequestParam(required = false) String diagnosisBasis) {
        // 1. 建档+病种绑定
        org.dromara.chronic.domain.bo.ChPatientDiseaseBo diseaseBo = new org.dromara.chronic.domain.bo.ChPatientDiseaseBo();
        diseaseBo.setPatientId(patientId);
        diseaseBo.setDiseaseCode(diseaseCode);
        diseaseBo.setIcdCode(icdCode);
        diseaseBo.setDiagnosisBasis(diagnosisBasis);
        diseaseBo.setIsComplication(false);
        patientProfileManager.bindDisease(diseaseBo);

        // 2. 触发风险评估（异步，可由后续定时任务补偿）
        org.dromara.chronic.domain.bo.ChRiskAssessmentBo riskBo = new org.dromara.chronic.domain.bo.ChRiskAssessmentBo();
        riskBo.setPatientId(patientId);
        riskBo.setDiseaseCode(diseaseCode);
        riskAssessmentManager.assess(riskBo);

        logSync("HIS_DIAGNOSIS", "INBOUND", "HIS", "SUCCESS",
            "确诊同步: patientId=" + patientId + ", diseaseCode=" + diseaseCode);

        return R.ok(patientId);
    }

    private void logSync(String syncType, String direction, String system, String status, String detail) {
        ChExternalSyncLog syncLog = new ChExternalSyncLog();
        syncLog.setSyncType(syncType);
        syncLog.setSyncDirection(direction);
        syncLog.setExternalSystem(system);
        syncLog.setSyncStatus(status);
        syncLog.setSyncDetail(StringUtils.substring(detail, 0, 500));
        syncLog.setSyncTime(new Date());
        externalSyncLogMapper.insert(syncLog);
    }
}
