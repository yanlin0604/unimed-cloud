package org.dromara.chronic.manager;

import org.dromara.chronic.domain.bo.ChArchiveShareApplyBo;
import org.dromara.chronic.domain.entity.ChArchiveShareApply;
import org.dromara.chronic.domain.vo.ChArchiveShareApplyVo;
import org.dromara.chronic.mapper.ChArchiveShareApplyMapper;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.workflow.api.RemoteWorkflowService;
import org.dromara.workflow.api.domain.RemoteStartProcess;
import org.dromara.workflow.api.domain.RemoteStartProcessReturn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ArchiveShareManager 测试
 * 验证调档申请创建、撤回、工作流回调、工作流集成
 *
 * @author unimed
 */
@ExtendWith(MockitoExtension.class)
class ArchiveShareManagerTest {

    @Mock
    private ChArchiveShareApplyMapper applyMapper;

    @Mock
    private ChAuditLogMapper auditLogMapper;

    @Mock
    private RemoteWorkflowService remoteWorkflowService;

    @InjectMocks
    private ArchiveShareManager archiveShareManager;

    @Test
    void shouldCreateApplyWithPendingStatus() {
        ChArchiveShareApplyBo bo = new ChArchiveShareApplyBo();
        bo.setPatientId(1L);
        bo.setApplyOrgId(100L);
        bo.setTargetOrgId(200L);
        bo.setApplyReason("跨机构调档");

        when(applyMapper.insert(any(ChArchiveShareApply.class))).thenAnswer(invocation -> {
            ChArchiveShareApply entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });
        // 工作流启动成功
        RemoteStartProcessReturn returnVal = new RemoteStartProcessReturn();
        returnVal.setInstanceId(999L);
        when(remoteWorkflowService.startWorkFlow(any(RemoteStartProcess.class))).thenReturn(returnVal);

        Long id = archiveShareManager.createApply(bo);
        assertNotNull(id);
        assertEquals(1L, id);
        verify(applyMapper).insert(any(ChArchiveShareApply.class));
        verify(remoteWorkflowService).startWorkFlow(any(RemoteStartProcess.class));
    }

    @Test
    void shouldContinueWhenWorkflowStartFails() {
        ChArchiveShareApplyBo bo = new ChArchiveShareApplyBo();
        bo.setPatientId(1L);
        bo.setApplyOrgId(100L);
        bo.setTargetOrgId(200L);

        when(applyMapper.insert(any(ChArchiveShareApply.class))).thenAnswer(invocation -> {
            ChArchiveShareApply entity = invocation.getArgument(0);
            entity.setId(2L);
            return 1;
        });
        // 工作流启动失败，仍应正常返回
        when(remoteWorkflowService.startWorkFlow(any(RemoteStartProcess.class)))
            .thenThrow(new RuntimeException("工作流服务不可用"));

        Long id = archiveShareManager.createApply(bo);
        assertNotNull(id);
        assertEquals(2L, id);
    }

    @Test
    void shouldRejectWithdrawOnTerminalStatus() {
        // APPROVED 是终端状态，不可撤回
        ChArchiveShareApply apply = new ChArchiveShareApply();
        apply.setId(1L);
        apply.setPatientId(1L);
        apply.setApprovalStatus("APPROVED");

        when(applyMapper.selectById(1L)).thenReturn(apply);

        assertThrows(RuntimeException.class, () -> {
            archiveShareManager.withdraw(1L);
        });
    }

    @Test
    void shouldRejectWithdrawOnRejectedStatus() {
        // REJECTED 是终端状态，不可撤回
        ChArchiveShareApply apply = new ChArchiveShareApply();
        apply.setId(4L);
        apply.setPatientId(1L);
        apply.setApprovalStatus("REJECTED");

        when(applyMapper.selectById(4L)).thenReturn(apply);

        assertThrows(RuntimeException.class, () -> {
            archiveShareManager.withdraw(4L);
        });
    }

    @Test
    void shouldAllowWithdrawOnPendingStatus() {
        ChArchiveShareApply apply = new ChArchiveShareApply();
        apply.setId(2L);
        apply.setPatientId(1L);
        apply.setApprovalStatus("PENDING");

        when(applyMapper.selectById(2L)).thenReturn(apply);
        when(applyMapper.updateById(any(ChArchiveShareApply.class))).thenReturn(1);

        archiveShareManager.withdraw(2L);
        verify(applyMapper).updateById(any(ChArchiveShareApply.class));
    }

    @Test
    void shouldAllowWithdrawOnApprovingStatus() {
        // APPROVING 不是终端状态，应允许撤回（design 要求终端审批前都可撤回）
        ChArchiveShareApply apply = new ChArchiveShareApply();
        apply.setId(5L);
        apply.setPatientId(1L);
        apply.setApprovalStatus("APPROVING");

        when(applyMapper.selectById(5L)).thenReturn(apply);
        when(applyMapper.updateById(any(ChArchiveShareApply.class))).thenReturn(1);

        archiveShareManager.withdraw(5L);
        verify(applyMapper).updateById(any(ChArchiveShareApply.class));
    }

    @Test
    void shouldUpdateStatusOnWorkflowCallback() {
        ChArchiveShareApply apply = new ChArchiveShareApply();
        apply.setId(3L);
        apply.setPatientId(1L);
        apply.setApprovalStatus("PENDING");

        when(applyMapper.selectById(3L)).thenReturn(apply);
        when(applyMapper.updateById(any(ChArchiveShareApply.class))).thenReturn(1);

        archiveShareManager.workflowCallback(3L, "APPROVED");
        verify(applyMapper).updateById(any(ChArchiveShareApply.class));
    }

    @Test
    void shouldSkipCallbackWhenStatusAlreadySet() {
        // 幂等：状态一致则跳过
        ChArchiveShareApply apply = new ChArchiveShareApply();
        apply.setId(6L);
        apply.setPatientId(1L);
        apply.setApprovalStatus("APPROVED");

        when(applyMapper.selectById(6L)).thenReturn(apply);

        archiveShareManager.workflowCallback(6L, "APPROVED");
        verify(applyMapper, never()).updateById(any(ChArchiveShareApply.class));
    }
}
