package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChArchiveShareApplyBo;
import org.dromara.chronic.domain.entity.ChArchiveShareApply;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.vo.ChArchiveShareApplyVo;
import org.dromara.chronic.mapper.ChArchiveShareApplyMapper;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.workflow.api.RemoteWorkflowService;
import org.dromara.workflow.api.domain.RemoteStartProcess;
import org.dromara.workflow.api.domain.RemoteStartProcessReturn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 调档审批管理器：申请→工作流→回调→状态同步
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveShareManager {

    private final ChArchiveShareApplyMapper applyMapper;
    private final ChAuditLogMapper auditLogMapper;
    @DubboReference(mock = "true")
    private RemoteWorkflowService remoteWorkflowService;

    /** 终端审批状态，不可撤回 */
    private static final Set<String> TERMINAL_STATUSES = Set.of("APPROVED", "REJECTED", "WITHDRAWN");

    @Transactional(rollbackFor = Exception.class)
    public Long createApply(ChArchiveShareApplyBo bo) {
        ChArchiveShareApply entity = MapstructUtils.convert(bo, ChArchiveShareApply.class);
        entity.setApprovalStatus("PENDING");
        applyMapper.insert(entity);
        logAudit("ARCHIVE_SHARE_APPLY", "CREATE", "申请调档: patientId=" + bo.getPatientId() + ", applyId=" + entity.getId());

        // 启动工作流审批
        try {
            RemoteStartProcess startProcess = new RemoteStartProcess();
            startProcess.setBusinessId(String.valueOf(entity.getId()));
            startProcess.setFlowCode("archive_share_approve");
            RemoteStartProcessReturn result = remoteWorkflowService.startWorkFlow(startProcess);
            if (result != null) {
                // 回填工作流实例ID，便于后续回调反查
                entity.setWorkflowInstanceId(result.getProcessInstanceId());
                applyMapper.updateById(entity);
                log.info("调档申请工作流已启动, applyId={}, instanceId={}", entity.getId(), result.getProcessInstanceId());
            }
        } catch (Exception e) {
            log.warn("调档申请工作流启动失败, applyId={}, 将依赖补偿任务", entity.getId(), e);
        }
        return entity.getId();
    }

    public ChArchiveShareApplyVo queryById(Long applyId) {
        return applyMapper.selectVoById(applyId);
    }

    public TableDataInfo<ChArchiveShareApplyVo> queryPageList(ChArchiveShareApplyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChArchiveShareApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPatientId() != null, ChArchiveShareApply::getPatientId, bo.getPatientId());
        lqw.eq(bo.getApplyOrgId() != null, ChArchiveShareApply::getApplyOrgId, bo.getApplyOrgId());
        lqw.eq(bo.getTargetOrgId() != null, ChArchiveShareApply::getTargetOrgId, bo.getTargetOrgId());
        lqw.eq(StringUtils.isNotBlank(bo.getApprovalStatus()), ChArchiveShareApply::getApprovalStatus, bo.getApprovalStatus());
        lqw.orderByDesc(ChArchiveShareApply::getCreateTime);
        return applyMapper.selectVoPage(pageQuery.build(), lqw);
    }

    @Transactional(rollbackFor = Exception.class)
    public Void withdraw(Long applyId) {
        ChArchiveShareApply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new RuntimeException("调档申请不存在");
        }
        if (TERMINAL_STATUSES.contains(apply.getApprovalStatus())) {
            throw new RuntimeException("当前状态不允许撤回: " + apply.getApprovalStatus());
        }
        apply.setApprovalStatus("WITHDRAWN");
        applyMapper.updateById(apply);
        logAudit("ARCHIVE_SHARE_WITHDRAW", "WITHDRAW", "撤回调档申请: applyId=" + applyId);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Void workflowCallback(Long applyId, String status) {
        ChArchiveShareApply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            log.warn("工作流回调但申请不存在, applyId={}", applyId);
            return null;
        }
        // 幂等：状态一致则跳过
        if (status.equals(apply.getApprovalStatus())) {
            return null;
        }
        apply.setApprovalStatus(status);
        applyMapper.updateById(apply);
        logAudit("ARCHIVE_SHARE_CALLBACK", "CALLBACK", "工作流回调更新状态: applyId=" + applyId + ", status=" + status);
        return null;
    }

    public Long findByWorkflowInstanceId(Long workflowInstanceId) {
        LambdaQueryWrapper<ChArchiveShareApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChArchiveShareApply::getWorkflowInstanceId, workflowInstanceId);
        ChArchiveShareApply apply = applyMapper.selectOne(lqw, false);
        return apply != null ? apply.getId() : null;
    }

    private void logAudit(String operationType, String operationTarget, String detail) {
        try {
            ChAuditLog auditLog = new ChAuditLog();
            auditLog.setOperationType(operationType);
            auditLog.setOperationTarget(operationTarget);
            auditLog.setOperationDetail(detail);
            auditLog.setOperatorId(LoginHelper.getUserId());
            auditLog.setOperatorName(LoginHelper.getUsername());
            auditLog.setOperatorIp(ServletUtils.getClientIP());
            auditLog.setOperationTime(new Date());
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.warn("审计日志写入失败", e);
        }
    }
}