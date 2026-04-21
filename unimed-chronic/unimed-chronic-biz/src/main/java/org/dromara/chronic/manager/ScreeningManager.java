package org.dromara.chronic.manager;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.bo.ChScreeningRecordBo;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.chronic.domain.entity.ChScreeningRecord;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.chronic.domain.vo.ChScreeningRecordVo;
import org.dromara.chronic.mapper.ChScreeningBatchMapper;
import org.dromara.chronic.mapper.ChScreeningRecordMapper;
import org.dromara.chronic.service.IChScreeningBatchService;
import org.dromara.chronic.service.IChScreeningRecordService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 筛查管理编排层
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ScreeningManager {

    private final IChScreeningBatchService screeningBatchService;
    private final IChScreeningRecordService screeningRecordService;
    private final ChScreeningBatchMapper screeningBatchMapper;
    private final ChScreeningRecordMapper screeningRecordMapper;
    private final PatientProfileManager patientProfileManager;

    public ChScreeningBatchVo startBatch(ChScreeningBatchBo bo) {
        if (ObjectUtil.isNull(bo.getStatus())) {
            bo.setStatus("ACTIVE");
        }
        screeningBatchService.insertByBo(bo);
        return screeningBatchService.queryById(bo.getBatchId());
    }

    public ChScreeningRecordVo saveRecord(ChScreeningRecordBo bo) {
        fillRiskResult(bo);
        if (ObjectUtil.isNull(bo.getEnrollStatus())) {
            bo.setEnrollStatus("PENDING");
        }
        return screeningRecordService.saveRecord(bo);
    }

    public List<ChScreeningRecordVo> batchUpload(List<ChScreeningRecordBo> list) {
        list.forEach(this::fillRiskResult);
        list.forEach(item -> {
            if (ObjectUtil.isNull(item.getEnrollStatus())) {
                item.setEnrollStatus("PENDING");
            }
        });
        return screeningRecordService.batchSave(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long enroll(Long recordId) {
        ChScreeningRecord record = screeningRecordMapper.selectById(recordId);
        if (ObjectUtil.isNull(record)) {
            throw new ServiceException("筛查记录不存在");
        }
        if (ObjectUtil.isNotNull(record.getEnrolledPatientId())) {
            return record.getEnrolledPatientId();
        }
        ChScreeningBatch batch = screeningBatchMapper.selectById(record.getBatchId());
        ChPatientProfileBo patientBo = new ChPatientProfileBo();
        patientBo.setName(record.getPatientName());
        patientBo.setIdCard(record.getIdCard());
        patientBo.setPhone(record.getPhone());
        patientBo.setGender(record.getGender());
        patientBo.setOrgId(batch == null ? null : batch.getOrgId());
        patientBo.setDoctorUserId(batch == null ? null : batch.getDoctorUserId());
        patientBo.setSource("SCREENING");
        patientBo.setManageStatus("PENDING_ENTRY");
        Long patientId = patientProfileManager.createArchive(patientBo, Collections.emptyList(), Collections.emptyList());
        record.setEnrolledPatientId(patientId);
        record.setEnrollStatus("ENROLLED");
        screeningRecordMapper.updateById(record);
        return patientId;
    }

    /**
     * Task 10 完成前先使用轻量规则识别高危等级，后续可替换为正式 RiskRuleEngine。
     */
    private void fillRiskResult(ChScreeningRecordBo bo) {
        Dict vitals = JsonUtils.parseMap(bo.getVitals());
        String symptoms = ObjectUtil.defaultIfNull(bo.getSymptoms(), "").toString();
        String riskLevel = "LOW";
        String conclusion = "指标基本稳定，建议常规健康宣教";
        if (ObjectUtil.isNotNull(vitals)) {
            Double systolic = toDouble(vitals.get("systolic"));
            Double diastolic = toDouble(vitals.get("diastolic"));
            Double glucose = toDouble(vitals.get("glucose"));
            if ((systolic != null && systolic >= 180D) || (diastolic != null && diastolic >= 110D)
                || (glucose != null && glucose >= 16.7D) || containsCriticalSymptoms(symptoms)) {
                riskLevel = "VERY_HIGH";
                conclusion = "高危筛查阳性，建议立即入组并进一步转诊评估";
            } else if ((systolic != null && systolic >= 160D) || (diastolic != null && diastolic >= 100D)
                || (glucose != null && glucose >= 11.1D)) {
                riskLevel = "HIGH";
                conclusion = "存在显著异常指标，建议优先入组慢病管理";
            } else if ((systolic != null && systolic >= 140D) || (diastolic != null && diastolic >= 90D)
                || (glucose != null && glucose >= 7.0D)) {
                riskLevel = "MEDIUM";
                conclusion = "存在异常趋势，建议随访复测并评估入组";
            }
        }
        bo.setRiskLevel(riskLevel);
        bo.setConclusion(conclusion);
    }

    private boolean containsCriticalSymptoms(String symptoms) {
        return symptoms.contains("胸痛") || symptoms.contains("呼吸困难") || symptoms.contains("意识障碍");
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
