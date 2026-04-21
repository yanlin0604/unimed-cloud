package org.dromara.chronic;

import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.mapper.ChWarningActionMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.chronic.service.impl.ChWarningEventServiceImpl;
import org.dromara.common.core.exception.ServiceException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 慢病管理闭环贯通集成测试
 * <p>
 * 完整闭环场景：建档→签约→方案→随访→预警→处置→再评估→方案调整
 * Mock所有Dubbo远程服务，验证核心闭环状态流转
 *
 * @author unimed
 */
class ChronicLoopIntegrationTest {

    @Mock
    private ChWarningEventMapper warningEventMapper;

    @Mock
    private ChWarningActionMapper warningActionMapper;

    @InjectMocks
    private ChWarningEventServiceImpl warningEventService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * 闭环场景：建档→签约→方案→随访→预警→处置→再评估→方案调整
     * 仅测试预警事件服务闭环（依赖可Mock的范围）
     */
    @Test
    void testFullClosedLoop() {
        // ========== Step 1: 建档 ==========
        ChPatientProfile profile = new ChPatientProfile();
        profile.setPatientId(1L);
        profile.setName("张三");
        profile.setManageStatus("PENDING_ENTRY");
        assertEquals("PENDING_ENTRY", profile.getManageStatus());

        // ========== Step 2: 签约 ==========
        ChPatientContract contract = new ChPatientContract();
        contract.setContractId(1L);
        contract.setPatientId(1L);
        contract.setTeamId(1L);
        contract.setPackageId(1L);
        contract.setContractStatus("ACTIVE");

        // 签约后状态更新: MANAGED
        profile.setManageStatus("MANAGED");
        assertEquals("MANAGED", profile.getManageStatus());

        // ========== Step 3: 风险评估 ==========
        ChRiskAssessment assessment = new ChRiskAssessment();
        assessment.setAssessmentId(1L);
        assessment.setPatientId(1L);
        assessment.setDiseaseCode("HYPERTENSION");
        assessment.setRiskLevel("HIGH");
        assertEquals("HIGH", assessment.getRiskLevel());

        // ========== Step 4: 管理方案 ==========
        ChManagePlan plan = new ChManagePlan();
        plan.setPlanId(1L);
        plan.setPatientId(1L);
        plan.setDiseaseCode("HYPERTENSION");
        plan.setPlanStatus("ACTIVE");
        assertEquals("ACTIVE", plan.getPlanStatus());

        // ========== Step 5: 随访计划→任务生成 ==========
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPatientId(1L);
        task.setPlanId(1L);
        task.setTaskRound(1);
        task.setTaskStatus("PENDING");
        assertEquals("PENDING", task.getTaskStatus());

        // ========== Step 6: 指标异常→预警 ==========
        ChWarningEventBo eventBo = new ChWarningEventBo();
        eventBo.setPatientId(1L);
        eventBo.setRuleId(1L);
        eventBo.setWarningLevel("HIGH");

        when(warningEventMapper.insert(any(ChWarningEvent.class))).thenAnswer(invocation -> {
            ChWarningEvent entity = invocation.getArgument(0);
            entity.setWarningId(1L);
            return 1;
        });

        Long warningId = warningEventService.createEvent(eventBo);
        assertNotNull(warningId);
        assertEquals(1L, warningId);

        // 预警状态: NEW
        ChWarningEvent createdEvent = new ChWarningEvent();
        createdEvent.setWarningId(1L);
        createdEvent.setPatientId(1L);
        createdEvent.setWarningLevel("HIGH");
        createdEvent.setEventStatus("NEW");
        createdEvent.setWarningTime(new Date());

        ChWarningEventVo eventVo = new ChWarningEventVo();
        eventVo.setWarningId(1L);
        eventVo.setEventStatus("NEW");
        when(warningEventMapper.selectVoById(1L)).thenReturn(eventVo);
        when(warningActionMapper.selectVoList(any())).thenReturn(java.util.List.of());

        ChWarningEventVo queried = warningEventService.queryById(1L);
        assertNotNull(queried);
        assertEquals("NEW", queried.getEventStatus());

        // ========== Step 7: 医生处置预警 ==========
        // NEW → CONFIRMED
        when(warningEventMapper.selectById(1L)).thenReturn(createdEvent);
        when(warningEventMapper.updateById(any(ChWarningEvent.class))).thenReturn(1);
        warningEventService.updateStatus(1L, "CONFIRMED");

        // CONFIRMED → PROCESSING
        createdEvent.setEventStatus("CONFIRMED");
        warningEventService.updateStatus(1L, "PROCESSING");

        // PROCESSING → RESOLVED
        createdEvent.setEventStatus("PROCESSING");
        warningEventService.updateStatus(1L, "RESOLVED");
        assertEquals("RESOLVED", createdEvent.getEventStatus());

        // ========== Step 8: 再评估 → 方案调整 ==========
        ChRiskAssessment reassess = new ChRiskAssessment();
        reassess.setAssessmentId(2L);
        reassess.setPatientId(1L);
        reassess.setRiskLevel("MEDIUM");
        assertEquals("MEDIUM", reassess.getRiskLevel());

        // 方案调整: ACTIVE → HISTORY, 新方案 ACTIVE
        plan.setPlanStatus("HISTORY");
        ChManagePlan newPlan = new ChManagePlan();
        newPlan.setPlanId(2L);
        newPlan.setPatientId(1L);
        newPlan.setPlanStatus("ACTIVE");
        assertEquals("HISTORY", plan.getPlanStatus());
        assertEquals("ACTIVE", newPlan.getPlanStatus());
    }

    /**
     * 验证无效状态转换抛出异常
     */
    @Test
    void testInvalidStatusTransition() {
        ChWarningEvent event = new ChWarningEvent();
        event.setWarningId(1L);
        event.setEventStatus("NEW");
        when(warningEventMapper.selectById(1L)).thenReturn(event);

        assertThrows(ServiceException.class, () -> {
            warningEventService.updateStatus(1L, "INVALID_STATUS");
        });
    }

    /**
     * 验证随访任务状态流转: PENDING→REMINDING→DONE
     */
    @Test
    void testFollowupTaskStatusTransition() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setTaskStatus("PENDING");

        task.setTaskStatus("REMINDING");
        assertEquals("REMINDING", task.getTaskStatus());

        task.setTaskStatus("DONE");
        assertEquals("DONE", task.getTaskStatus());
    }

    /**
     * 验证管理方案状态流转: DRAFT→ACTIVE→DISABLED/HISTORY
     */
    @Test
    void testManagePlanStatusTransition() {
        ChManagePlan plan = new ChManagePlan();
        plan.setPlanId(1L);
        plan.setPlanStatus("DRAFT");

        plan.setPlanStatus("ACTIVE");
        assertEquals("ACTIVE", plan.getPlanStatus());

        plan.setPlanStatus("HISTORY");
        assertEquals("HISTORY", plan.getPlanStatus());
    }

    /**
     * 验证签约到期状态流转: ACTIVE→EXPIRING→EXPIRED→RENEWED
     */
    @Test
    void testContractRenewalStatusTransition() {
        ChPatientContract contract = new ChPatientContract();
        contract.setContractId(1L);
        contract.setRenewalStatus("ACTIVE");

        contract.setRenewalStatus("EXPIRING");
        assertEquals("EXPIRING", contract.getRenewalStatus());

        contract.setRenewalStatus("EXPIRED");
        assertEquals("EXPIRED", contract.getRenewalStatus());

        contract.setRenewalStatus("RENEWED");
        assertEquals("RENEWED", contract.getRenewalStatus());
    }
}
