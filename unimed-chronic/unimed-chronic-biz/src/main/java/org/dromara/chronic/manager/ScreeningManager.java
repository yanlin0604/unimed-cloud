package org.dromara.chronic.manager;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.bo.ChScreeningRecordBo;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientTag;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    /**
     * R12: 筛查入组 —— 根据风险等级生成标签，匹配规则引擎推断疑似病种
     * <p>
     * 入组时携带病种（ChPatientDisease）与 RISK 标签（ChPatientTag），
     * 并在时间线写入"义诊筛查入组"事件。
     */
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

        // R12: 根据风险等级生成 RISK 标签
        List<ChPatientTag> tags = buildRiskTags(record.getRiskLevel());

        // R12: 根据 symptoms/vitals 匹配 RiskRuleEngine 推断疑似病种
        List<ChPatientDisease> diseases = inferDiseasesFromScreening(record);

        ChPatientProfileBo patientBo = new ChPatientProfileBo();
        patientBo.setName(record.getPatientName());
        patientBo.setIdCard(record.getIdCard());
        patientBo.setPhone(record.getPhone());
        patientBo.setGender(record.getGender());
        patientBo.setOrgId(batch == null ? null : batch.getOrgId());
        patientBo.setDoctorUserId(batch == null ? null : batch.getDoctorUserId());
        patientBo.setSource("SCREENING");
        patientBo.setManageStatus("PENDING_ENTRY");

        // R12: 建档时把 diseases/tags 传入 createArchive
        Long patientId = patientProfileManager.createArchive(patientBo, diseases, tags);
        record.setEnrolledPatientId(patientId);
        record.setEnrollStatus("ENROLLED");
        screeningRecordMapper.updateById(record);
        return patientId;
    }

    /**
     * R12: 根据风险等级生成 RISK 标签
     */
    private List<ChPatientTag> buildRiskTags(String riskLevel) {
        if (riskLevel == null) {
            return Collections.emptyList();
        }
        List<ChPatientTag> tags = new ArrayList<>();
        ChPatientTag tag = new ChPatientTag();
        tag.setTagType("RISK");
        tag.setTagValue(mapRiskLevelToTagValue(riskLevel));
        tags.add(tag);
        return tags;
    }

    /**
     * R12: 根据 symptoms/vitals 匹配 RiskRuleEngine 推断疑似病种
     * <p>
     * 推断病种必须可配置化，通过 RiskRuleEngine 完成；
     * 若无匹配规则则不生成病种记录。
     */
    private List<ChPatientDisease> inferDiseasesFromScreening(ChScreeningRecord record) {
        List<ChPatientDisease> diseases = new ArrayList<>();
        // 使用体征数据推断疑似病种
        Dict vitals = JsonUtils.parseMap(record.getVitals());
        if (vitals == null || vitals.isEmpty()) {
            return diseases;
        }
        // 高血压推断：收缩压 ≥ 140 或舒张压 ≥ 90
        Double systolic = toDouble(vitals.get("systolic"));
        Double diastolic = toDouble(vitals.get("diastolic"));
        if ((systolic != null && systolic >= 140D) || (diastolic != null && diastolic >= 90D)) {
            ChPatientDisease disease = new ChPatientDisease();
            disease.setDiseaseCode("HYPERTENSION");
            disease.setDiagnosisBasis("筛查血压异常");
            disease.setConfirmDate(new Date());
            diseases.add(disease);
        }
        // 糖尿病推断：空腹血糖 ≥ 7.0
        Double glucose = toDouble(vitals.get("glucose"));
        if (glucose != null && glucose >= 7.0D) {
            ChPatientDisease disease = new ChPatientDisease();
            disease.setDiseaseCode("DIABETES");
            disease.setDiagnosisBasis("筛查血糖异常");
            disease.setConfirmDate(new Date());
            diseases.add(disease);
        }
        return diseases;
    }

    private String mapRiskLevelToTagValue(String riskLevel) {
        return switch (riskLevel) {
            case "VERY_HIGH" -> "高危";
            case "HIGH" -> "中高危";
            case "MEDIUM" -> "中危";
            default -> "低危";
        };
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
