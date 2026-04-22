package org.dromara.chronic.manager;

import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChPatientTimelineService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.resource.api.RemoteMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ContractHistoryManager 测试
 * 验证签约历史查询、续约提醒（含消息推送 + 审计日志）
 *
 * @author unimed
 */
@ExtendWith(MockitoExtension.class)
class ContractHistoryManagerTest {

    @Mock
    private IChPatientContractService contractService;

    @Mock
    private IChPatientTimelineService timelineService;

    @Mock
    private ChAuditLogMapper auditLogMapper;

    @Mock
    private RemoteMessageService remoteMessageService;

    @Mock
    private ChPatientProfileMapper patientProfileMapper;

    @InjectMocks
    private ContractHistoryManager contractHistoryManager;

    @Test
    void shouldReturnCurrentContract() {
        ChPatientContractVo vo = new ChPatientContractVo();
        vo.setContractId(1L);
        vo.setPatientId(100L);
        vo.setContractStatus("ACTIVE");
        when(contractService.queryCurrentContract(100L)).thenReturn(vo);

        ChPatientContractVo result = contractHistoryManager.queryCurrentContract(100L);
        assertNotNull(result);
        assertEquals(1L, result.getContractId());
    }

    @Test
    void shouldSendRenewalReminderWithMessageAndAudit() {
        ChPatientContractVo contract = new ChPatientContractVo();
        contract.setContractId(10L);
        contract.setPatientId(200L);
        contract.setContractStatus("ACTIVE");
        contract.setRenewalStatus("EXPIRING");
        when(contractService.queryById(10L)).thenReturn(contract);

        ChPatientProfile profile = new ChPatientProfile();
        profile.setPatientId(200L);
        profile.setDoctorUserId(300L);
        when(patientProfileMapper.selectById(200L)).thenReturn(profile);

        contractHistoryManager.sendRenewalReminder(10L);

        // 验证时间线事件写入
        verify(timelineService).recordEvent(eq(200L), eq("RENEWAL_REMINDER"), eq("续约提醒"), any(), any());
        // 验证消息推送给责任医生
        verify(remoteMessageService).publishMessage(eq(List.of(300L)), any(String.class));
        // 验证审计日志写入
        verify(auditLogMapper).insert(any(ChAuditLog.class));
    }

    @Test
    void shouldNotSendMessageWhenNoDoctorAssigned() {
        ChPatientContractVo contract = new ChPatientContractVo();
        contract.setContractId(11L);
        contract.setPatientId(201L);
        when(contractService.queryById(11L)).thenReturn(contract);

        // 患者没有责任医生
        ChPatientProfile profile = new ChPatientProfile();
        profile.setPatientId(201L);
        profile.setDoctorUserId(null);
        when(patientProfileMapper.selectById(201L)).thenReturn(profile);

        contractHistoryManager.sendRenewalReminder(11L);

        // 消息不应推送
        verify(remoteMessageService, never()).publishMessage(any(), any(String.class));
        // 审计日志仍应写入
        verify(auditLogMapper).insert(any(ChAuditLog.class));
    }

    @Test
    void shouldHandleNullContractGracefully() {
        when(contractService.queryById(999L)).thenReturn(null);

        contractHistoryManager.sendRenewalReminder(999L);

        // 不应写时间线、不应推送消息
        verify(timelineService, never()).recordEvent(anyLong(), any(), any(), any(), any());
        verify(remoteMessageService, never()).publishMessage(any(), any(String.class));
    }

    @Test
    void shouldContinueWhenMessagePushFails() {
        ChPatientContractVo contract = new ChPatientContractVo();
        contract.setContractId(12L);
        contract.setPatientId(202L);
        when(contractService.queryById(12L)).thenReturn(contract);

        ChPatientProfile profile = new ChPatientProfile();
        profile.setPatientId(202L);
        profile.setDoctorUserId(301L);
        when(patientProfileMapper.selectById(202L)).thenReturn(profile);

        // 消息推送抛异常
        doThrow(new RuntimeException("消息服务不可用")).when(remoteMessageService).publishMessage(any(), any(String.class));

        // 不应抛出异常
        assertDoesNotThrow(() -> contractHistoryManager.sendRenewalReminder(12L));

        // 审计日志仍应写入
        verify(auditLogMapper).insert(any(ChAuditLog.class));
    }

    @Test
    void shouldQueryContractTimelineByEventTypes() {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);

        TableDataInfo<ChPatientTimelineVo> mockResult = new TableDataInfo<>();
        when(timelineService.queryPageListByEventTypes(eq(100L), eq(List.of("SIGN", "RENEWAL_REMINDER", "CONTRACT_EXPIRED")), any(PageQuery.class)))
            .thenReturn(mockResult);

        TableDataInfo<ChPatientTimelineVo> result = contractHistoryManager.queryContractTimeline(100L, pageQuery);
        assertNotNull(result);
        verify(timelineService).queryPageListByEventTypes(eq(100L), eq(List.of("SIGN", "RENEWAL_REMINDER", "CONTRACT_EXPIRED")), any(PageQuery.class));
    }
}
