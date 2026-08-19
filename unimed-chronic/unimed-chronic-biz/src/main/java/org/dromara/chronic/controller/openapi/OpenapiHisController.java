package org.dromara.chronic.controller.openapi;

import org.dromara.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.entity.ChExternalSyncLog;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.manager.EncounterManager;
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

import java.util.Collections;
import java.util.Date;

/**
 * HIS 同步开放接口
 * <p>
 * 本控制器遵循两条硬性规则：
 * <ol>
 *   <li><b>幂等</b>：同一 id_card 或 sourceBizNo 重复推送 <b>不</b> 产生重复档案/记录。</li>
 *   <li><b>异步</b>：HIS 类接口必须在秒级内返回，耗时动作（风险评估、方案草案等）
 *       通过 {@link RiskAssessmentManager#assessAsync} 走 {@code chronicAsyncExecutor} 异步执行。</li>
 * </ol>
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-开放接口-HIS同步")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiHisController extends BaseController {

    private final PatientProfileManager patientProfileManager;
    private final RiskAssessmentManager riskAssessmentManager;
    private final EncounterManager encounterManager;
    private final ChExternalSyncLogMapper externalSyncLogMapper;

    @Operation(summary = "HIS患者信息同步")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/his/patient/sync")
    public R<Long> patientSync(@Validated @RequestBody ChPatientProfileBo bo) {
        try {
            ChPatientProfile existing = patientProfileManager.findByIdCard(bo.getIdCard());
            if (existing != null) {
                Long patientId = patientProfileManager.updateArchive(bo, existing.getPatientId());
                logSync("HIS_PATIENT", "INBOUND", "HIS", "UPDATED",
                    "更新患者: name=" + bo.getName() + ", idCard=" + bo.getIdCard() + ", patientId=" + patientId);
                return R.ok(patientId);
            }
            Long patientId = patientProfileManager.createArchive(bo, Collections.emptyList(), Collections.emptyList());
            logSync("HIS_PATIENT", "INBOUND", "HIS", "CREATED",
                "建档患者: name=" + bo.getName() + ", idCard=" + bo.getIdCard() + ", patientId=" + patientId);
            return R.ok(patientId);
        } catch (Exception ex) {
            log.warn("[chronic] HIS患者同步失败 idCard={} msg={}", bo.getIdCard(), ex.getMessage(), ex);
            logSync("HIS_PATIENT", "INBOUND", "HIS", "FAIL",
                "患者同步失败: idCard=" + bo.getIdCard() + ", error=" + ex.getMessage());
            return R.fail("HIS患者同步失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "HIS确诊结果同步")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/his/diagnosis/confirm")
    public R<Long> diagnosisConfirm(
            @Parameter(description = "患者ID") @RequestParam Long patientId,
            @Parameter(description = "病种编码") @RequestParam String diseaseCode,
            @Parameter(description = "ICD编码") @RequestParam String icdCode,
            @Parameter(description = "诊断依据") @RequestParam(required = false) String diagnosisBasis) {
        try {
            // —— 同步部分：绑定病种 + 写 syncLog，确保 2s 内返回 HIS ——
            ChPatientDiseaseBo diseaseBo = new ChPatientDiseaseBo();
            diseaseBo.setPatientId(patientId);
            diseaseBo.setDiseaseCode(diseaseCode);
            diseaseBo.setIcdCode(icdCode);
            diseaseBo.setDiagnosisBasis(diagnosisBasis);
            diseaseBo.setIsComplication(false);
            patientProfileManager.bindDisease(diseaseBo);

            logSync("HIS_DIAGNOSIS", "INBOUND", "HIS", "SUCCESS",
                "确诊同步: patientId=" + patientId + ", diseaseCode=" + diseaseCode + ", icd=" + icdCode);

            // —— 异步部分：风险评估（后续可进一步扩展方案草案生成）——
            ChRiskAssessmentBo riskBo = new ChRiskAssessmentBo();
            riskBo.setPatientId(patientId);
            riskBo.setDiseaseCode(diseaseCode);
            riskAssessmentManager.assessAsync(riskBo);

            return R.ok(patientId);
        } catch (Exception ex) {
            log.warn("[chronic] HIS确诊同步失败 patientId={} diseaseCode={} msg={}",
                patientId, diseaseCode, ex.getMessage(), ex);
            logSync("HIS_DIAGNOSIS", "INBOUND", "HIS", "FAIL",
                "确诊同步失败: patientId=" + patientId + ", diseaseCode=" + diseaseCode + ", error=" + ex.getMessage());
            return R.fail("HIS确诊同步失败: " + ex.getMessage());
        }
    }

    @Operation(summary = "HIS诊疗记录同步（幂等）")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/his/encounter-sync")
    public R<Long> encounterSync(@Validated @RequestBody ChEncounterRecordBo bo) {
        bo.setSourceType("HIS");
        Long existingId = encounterManager.findBySourceBizNo(bo.getSourceBizNo(), bo.getPatientId());
        if (existingId != null) {
            logSync("HIS_ENCOUNTER", "INBOUND", "HIS", "SKIP",
                "幂等跳过: sourceBizNo=" + bo.getSourceBizNo() + ", encounterId=" + existingId);
            return R.ok(existingId);
        }
        Long encounterId = encounterManager.saveDraft(bo, bo.getDiagnosisList());
        logSync("HIS_ENCOUNTER", "INBOUND", "HIS", "SUCCESS",
            "同步诊疗记录: patientId=" + bo.getPatientId() + ", encounterId=" + encounterId);
        return R.ok(encounterId);
    }

    private void logSync(String syncType, String direction, String system, String status, String detail) {
        try {
            ChExternalSyncLog syncLog = new ChExternalSyncLog();
            syncLog.setSyncType(syncType);
            syncLog.setSyncDirection(direction);
            syncLog.setExternalSystem(system);
            syncLog.setSyncStatus(status);
            syncLog.setSyncDetail(StringUtils.substring(detail, 0, 500));
            syncLog.setSyncTime(new Date());
            externalSyncLogMapper.insert(syncLog);
        } catch (Exception ex) {
            // 日志落库失败不影响主流程
            log.warn("[chronic] 写 ch_external_sync_log 失败 type={} status={} msg={}",
                syncType, status, ex.getMessage());
        }
    }
}
