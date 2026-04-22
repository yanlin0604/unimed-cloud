package org.dromara.chronic.dubbo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.chronic.api.RemoteChronicService;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChRiskAssessment;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChRiskAssessmentMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 慢病管理远程服务实现
 * <p>
 * 仅暴露读方法，写操作通过 REST API + 鉴权
 *
 * @author unimed
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteChronicServiceImpl implements RemoteChronicService {

    private final ChPatientProfileMapper patientProfileMapper;
    private final ChWarningEventMapper warningEventMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChRiskAssessmentMapper riskAssessmentMapper;
    private final ChPatientDiseaseMapper patientDiseaseMapper;

    @Override
    public Map<String, Object> getPatientSummary(Long patientId) {
        ChPatientProfile patient = patientProfileMapper.selectById(patientId);
        if (patient == null) {
            throw new ServiceException("患者不存在");
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("patientId", patient.getPatientId());
        summary.put("name", patient.getName());
        summary.put("manageStatus", patient.getManageStatus());
        summary.put("orgId", patient.getOrgId());

        // R5: 补齐 riskLevel（从最新风险评估记录读取）
        ChRiskAssessment latestAssessment = riskAssessmentMapper.selectOne(
            Wrappers.<ChRiskAssessment>lambdaQuery()
                .eq(ChRiskAssessment::getPatientId, patientId)
                .orderByDesc(ChRiskAssessment::getCreateTime)
                .last("LIMIT 1")
        );
        summary.put("riskLevel", latestAssessment != null ? latestAssessment.getRiskLevel() : null);

        // R5: 补齐 primaryDiseaseCode（从患者主病种读取，is_complication=false 取第一条）
        ChPatientDisease primaryDisease = patientDiseaseMapper.selectOne(
            Wrappers.<ChPatientDisease>lambdaQuery()
                .eq(ChPatientDisease::getPatientId, patientId)
                .eq(ChPatientDisease::getIsComplication, false)
                .orderByAsc(ChPatientDisease::getId)
                .last("LIMIT 1")
        );
        summary.put("primaryDiseaseCode", primaryDisease != null ? primaryDisease.getDiseaseCode() : null);

        return summary;
    }

    @Override
    public String getPatientRiskLevel(Long patientId) {
        // R5: 从 ch_risk_assessment 最新记录读取真实 riskLevel
        ChRiskAssessment latest = riskAssessmentMapper.selectOne(
            Wrappers.<ChRiskAssessment>lambdaQuery()
                .eq(ChRiskAssessment::getPatientId, patientId)
                .orderByDesc(ChRiskAssessment::getCreateTime)
                .last("LIMIT 1")
        );
        return latest != null && latest.getRiskLevel() != null ? latest.getRiskLevel() : "UNKNOWN";
    }

    @Override
    public Map<String, Long> getPatientFollowupStatus(Long patientId) {
        Map<String, Long> status = new HashMap<>();
        status.put("pendingCount", followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .eq(ChFollowupTask::getTaskStatus, "PENDING")
        ));
        status.put("doneCount", followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .eq(ChFollowupTask::getTaskStatus, "DONE")
        ));
        status.put("overdueCount", followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .eq(ChFollowupTask::getTaskStatus, "OVERDUE")
        ));
        return status;
    }

    @Override
    public Long getActiveWarningCount(Long orgId) {
        // R5: 按 orgId 过滤 —— 使用 apply 参数化子查询避免 SQL 注入
        return warningEventMapper.selectCount(
            Wrappers.<ChWarningEvent>lambdaQuery()
                .apply("patient_id IN (SELECT patient_id FROM ch_patient_profile WHERE org_id = {0} AND del_flag = '0')", orgId)
                .in(ChWarningEvent::getEventStatus, "NEW", "CONFIRMED", "PROCESSING")
        );
    }

    @Override
    public Long getManagedPatientCount(Long orgId) {
        return patientProfileMapper.selectCount(
            Wrappers.<ChPatientProfile>lambdaQuery()
                .eq(ChPatientProfile::getOrgId, orgId)
                .eq(ChPatientProfile::getManageStatus, "MANAGED")
        );
    }
}
