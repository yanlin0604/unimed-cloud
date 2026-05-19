package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChEncounterDiagnosisBo;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.entity.ChEncounterRecord;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.service.IChEncounterRecordService;
import org.dromara.chronic.service.impl.ChPatientTimelineServiceImpl;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 诊疗记录管理器：诊疗记录 CRUD → 时间线写入 → 事件触发点预留
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EncounterManager {

    private final IChEncounterRecordService encounterRecordService;
    private final ChPatientTimelineServiceImpl timelineService;
    private final ChAuditLogMapper auditLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(ChEncounterRecordBo bo, List<ChEncounterDiagnosisBo> diagnosisList) {
        return encounterRecordService.saveDraft(bo, diagnosisList);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long submit(Long encounterId, Long operatorId) {
        ChEncounterRecord record = encounterRecordService.getById(encounterId);
        if (record == null) {
            throw new RuntimeException("诊疗记录不存在");
        }
        // 幂等：已提交则直接返回，不重复写时间线
        if ("SUBMITTED".equals(record.getSubmitStatus())) {
            return encounterId;
        }
        Long id = encounterRecordService.submit(encounterId);
        writeTimeline(encounterId, operatorId);
        logAudit("ENCOUNTER_SUBMIT", "ENCOUNTER", "提交诊疗记录: encounterId=" + encounterId, operatorId);
        return id;
    }

    public ChEncounterRecord getById(Long encounterId) {
        return encounterRecordService.getById(encounterId);
    }

    public ChEncounterRecordVo queryById(Long encounterId) {
        return encounterRecordService.queryById(encounterId);
    }

    public TableDataInfo<ChEncounterRecordVo> queryPageList(ChEncounterRecordBo bo, PageQuery pageQuery) {
        return encounterRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 患者最近一次诊疗记录（直接查 ch_encounter_record，避免时间线缺事件导致总览空的问题）
     */
    public ChEncounterRecordVo queryLatestByPatientId(Long patientId) {
        return encounterRecordService.queryLatestByPatientId(patientId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long updateDraft(ChEncounterRecordBo bo, List<ChEncounterDiagnosisBo> diagnosisList) {
        return encounterRecordService.updateById(bo, diagnosisList);
    }

    /**
     * 删除诊疗记录（仅允许草稿）
     * <ul>
     *   <li>记录不存在：幂等返回 encounterId，不抛错</li>
     *   <li>已提交：抛 ServiceException，由全局异常处理转 R.fail</li>
     *   <li>草稿：级联清空诊断子表 + 删除主表 + 写审计日志</li>
     * </ul>
     */
    @Transactional(rollbackFor = Exception.class)
    public Long delete(Long encounterId, Long operatorId) {
        ChEncounterRecord record = encounterRecordService.getById(encounterId);
        if (record == null) {
            return encounterId;
        }
        if ("SUBMITTED".equals(record.getSubmitStatus())) {
            throw new ServiceException("已提交的诊疗记录不允许删除");
        }
        encounterRecordService.deleteById(encounterId);
        logAudit("ENCOUNTER_DELETE", "ENCOUNTER", "删除诊疗记录草稿: encounterId=" + encounterId, operatorId);
        return encounterId;
    }

    private void writeTimeline(Long encounterId, Long operatorId) {
        try {
            ChEncounterRecord record = encounterRecordService.getById(encounterId);
            if (record != null) {
                timelineService.recordEvent(
                    record.getPatientId(),
                    "ENCOUNTER",
                    "门诊诊疗记录",
                    "就诊类型: " + record.getEncounterType() + ", 就诊时间: " + record.getEncounterTime(),
                    LocalDateTime.now()
                );
            }
        } catch (Exception e) {
            log.warn("诊疗记录提交后时间线写入失败, encounterId={}", encounterId, e);
        }
    }

    public Long findBySourceBizNo(String sourceBizNo, Long patientId) {
        if (sourceBizNo == null) {
            return null;
        }
        ChEncounterRecord record = encounterRecordService.queryBySourceBizNo(sourceBizNo, patientId);
        return record != null ? record.getId() : null;
    }

    private void logAudit(String operationType, String operationTarget, String detail, Long operatorId) {
        try {
            ChAuditLog auditLog = new ChAuditLog();
            auditLog.setOperationType(operationType);
            auditLog.setOperationTarget(operationTarget);
            auditLog.setOperationDetail(detail);
            auditLog.setOperatorId(operatorId);
            auditLog.setOperationTime(new Date());
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.warn("审计日志写入失败", e);
        }
    }
}