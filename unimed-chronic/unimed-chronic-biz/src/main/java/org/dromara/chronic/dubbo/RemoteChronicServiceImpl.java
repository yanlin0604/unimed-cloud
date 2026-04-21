package org.dromara.chronic.dubbo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.chronic.api.RemoteChronicService;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
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

    @Override
    public Map<String, Object> getPatientSummary(Long patientId) {
        ChPatientProfile patient = patientProfileMapper.selectById(patientId);
        if (patient == null) {
            throw new ServiceException("患者不存在");
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("patientId", patient.getPatientId());
        summary.put("name", patient.getName());
        summary.put("idCard", patient.getIdCard());
        summary.put("manageStatus", patient.getManageStatus());
        summary.put("orgId", patient.getOrgId());
        return summary;
    }

    @Override
    public String getPatientRiskLevel(Long patientId) {
        // 查询最新风险评估记录的风险等级
        ChPatientProfile patient = patientProfileMapper.selectById(patientId);
        if (patient == null) {
            return "UNKNOWN";
        }
        // 从manageStatus推断风险等级（简化实现）
        return patient.getManageStatus() != null ? patient.getManageStatus() : "UNKNOWN";
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
        return warningEventMapper.selectCount(
            Wrappers.<ChWarningEvent>lambdaQuery()
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
