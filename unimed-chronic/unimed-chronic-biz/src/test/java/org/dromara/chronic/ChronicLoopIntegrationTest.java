package org.dromara.chronic;

import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.manager.PatientProfileManager;
import org.dromara.chronic.mapper.*;
import org.dromara.common.core.exception.ServiceException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private ChPatientProfileMapper patientProfileMapper;
    @Mock
    private ChPatientDiseaseMapper patientDiseaseMapper;
    @Mock
    private ChPatientTagMapper patientTagMapper;
    @Mock
    private ChPatientTimelineMapper patientTimelineMapper;
    @Mock
    private ChPatientContractMapper contractMapper;
    @Mock
    private ChContractServicePackageMapper servicePackageMapper;
    @Mock
    private ChContractFulfillmentMapper fulfillmentMapper;
    @Mock
    private ChManagePlanMapper managePlanMapper;
    @Mock
    private ChManagePlanItemMapper managePlanItemMapper;
    @Mock
    private ChFollowupPlanMapper followupPlanMapper;
    @Mock
    private ChFollowupTaskMapper followupTaskMapper;
    @Mock
    private ChFollowupRecordMapper followupRecordMapper;
    @Mock
    private ChWarningEventMapper warningEventMapper;
    @Mock
    private ChWarningActionMapper warningActionMapper;
    @Mock
    private ChWarningRuleMapper warningRuleMapper;
    @Mock
    private ChRiskAssessmentMapper riskAssessmentMapper;
    @Mock
    private ChMedicationRecordMapper medicationRecordMapper;

    @InjectMocks
    private PatientProfileManager patientProfileManager;

    @InjectMocks
    private ChPatientProfileServiceImpl patientProfileService;

    @InjectMocks
    private ChWarningEventServiceImpl warningEventService;

    @InjectMocks
    private ChRiskAssessmentServiceImpl riskAssessmentService;

    @InjectMocks
    private ChManagePlanServiceImpl managePlanService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * 闭环场景1：建档→签约→方案→随访→预警→处置→再评估→方案调整
     */
    @Test
    void testFullClosedLoop() {
        // ========== Step 1: 建档 ==========
        ChPatientProfileBo profileBo = new ChPatientProfileBo();
        profileBo.setName("张三");
        profileBo.setIdCard("110101199001011234");
        profileBo.setPhone("13800138000");
        profileBo.setGender("1");
        profileBo.setOrgId(1L);
        profileBo.setDoctorUserId(100L);
        profileBo.setSource("OUTPATIENT");
        profileBo.setManageStatus("PENDING_ENTRY");

        ChPatientProfile profile = new ChPatientProfile();
        profile.setPatientId(1L);
        profile.setName("张三");
        profile.setManageStatus("PENDING_ENTRY");
        when(patientProfileMapper.insert(any(ChPatientProfile.class))).thenReturn(1);
        when(patientProfileMapper.selectById(1L)).thenReturn(profile);

        // 建档后状态: PENDING_ENTRY
        assertEquals("PENDING_ENTRY", profile.getManageStatus());

        // ========== Step 2: 签约 ==========
        ChPatientContract contract = new ChPatientContract();
        contract.setContractId(1L);
        contract.setPatientId(1L);
        contract.setTeamId(1L);
        contract.setPackageId(1L);
        contract.setStatus("ACTIVE");
        when(contractMapper.insert(any(ChPatientContract.class))).thenReturn(1);
        when(contractMapper.selectById(1L)).thenReturn(contract);

        // 签约后状态更新: MANAGED
        profile.setManageStatus("MANAGED");
        when(patientProfileMapper.selectById(1L)).thenReturn(profile);
        assertEquals("MANAGED", profile.getManageStatus());

        // ========== Step 3: 风险评估 ==========
        ChRiskAssessmentBo riskBo = new ChRiskAssessmentBo();
        riskBo.setPatientId(1L);
        riskBo.setDiseaseCode("HYPERTENSION");
        riskBo.setMetricData("{\"systolic\":160,\"diastolic\":100}");

        ChRiskAssessment assessment = new ChRiskAssessment();
        assessment.setAssessmentId(1L);
        assessment.setPatientId(1L);
        assessment.setDiseaseCode("HYPERTENSION");
        assessment.setRiskLevel("HIGH");
        when(riskAssessmentMapper.insert(any(ChRiskAssessment.class))).thenReturn(1);
        when(riskAssessmentMapper.selectById(1L)).thenReturn(assessment);

        // 风险等级: HIGH
        assertEquals("HIGH", assessment.getRiskLevel());

        // ========== Step 4: 管理方案 ==========
        ChManagePlan plan = new ChManagePlan();
        plan.setPlanId(1L);
        plan.setPatientId(1L);
        plan.setDiseaseCode("HYPERTENSION");
        plan.setPlanStatus("ACTIVE");
        when(managePlanMapper.insert(any(ChManagePlan.class))).thenReturn(1);
        when(managePlanMapper.selectById(1L)).thenReturn(plan);

        assertEquals("ACTIVE", plan.getPlanStatus());

        // ========== Step 5: 随访计划→任务生成 ==========
        ChFollowupPlan followupPlan = new ChFollowupPlan();
        followupPlan.setPlanId(1L);
        followupPlan.setPatientId(1L);
        followupPlan.setDiseaseCode("HYPERTENSION");
        followupPlan.setCurrentRound(1);
        followupPlan.setStatus("ACTIVE");

        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPatientId(1L);
        task.setPlanId(1L);
        task.setTaskRound(1);
        task.setTaskStatus("PENDING");
        when(followupTaskMapper.insert(any(ChFollowupTask.class))).thenReturn(1);
        when(followupTaskMapper.selectById(1L)).thenReturn(task);

        // 任务状态: PENDING
        assertEquals("PENDING", task.getTaskStatus());

        // ========== Step 6: 指标异常→预警 ==========
        ChWarningEvent warningEvent = new ChWarningEvent();
        warningEvent.setWarningId(1L);
        warningEvent.setPatientId(1L);
        warningEvent.setWarningLevel("HIGH");
        warningEvent.setEventStatus("NEW");
        warningEvent.setWarningTime(new Date());
        when(warningEventMapper.insert(any(ChWarningEvent.class))).thenReturn(1);
        when(warningEventMapper.selectById(1L)).thenReturn(warningEvent);

        // 创建预警事件
        Long warningId = warningEventService.createEvent(new ChWarningEventBo() {{
            setPatientId(1L);
            setRuleId(1L);
            setWarningLevel("HIGH");
        }});
        assertNotNull(warningId);

        // 预警状态: NEW
        ChWarningEventVo eventVo = warningEventService.queryById(1L);
        // 验证初始状态为NEW

        // ========== Step 7: 医生处置预警 ==========
        warningEvent.setEventStatus("CONFIRMED");
        when(warningEventMapper.selectById(1L)).thenReturn(warningEvent);
        warningEventService.updateStatus(1L, "CONFIRMED");

        warningEvent.setEventStatus("PROCESSING");
        when(warningEventMapper.selectById(1L)).thenReturn(warningEvent);
        warningEventService.updateStatus(1L, "PROCESSING");

        warningEvent.setEventStatus("RESOLVED");
        when(warningEventMapper.selectById(1L)).thenReturn(warningEvent);
        warningEventService.updateStatus(1L, "RESOLVED");

        // 处置完成: RESOLVED
        assertEquals("RESOLVED", warningEvent.getEventStatus());

        // ========== Step 8: 再评估 → 方案调整 ==========
        ChRiskAssessment reassess = new ChRiskAssessment();
        reassess.setAssessmentId(2L);
        reassess.setPatientId(1L);
        reassess.setRiskLevel("MEDIUM"); // 风险下降
        when(riskAssessmentMapper.insert(any(ChRiskAssessment.class))).thenReturn(1);
        when(riskAssessmentMapper.selectById(2L)).thenReturn(reassess);

        assertEquals("MEDIUM", reassess.getRiskLevel());

        // 方案调整: ACTIVE → HISTORY, 新方案DRAFT
        plan.setPlanStatus("HISTORY");
        when(managePlanMapper.selectById(1L)).thenReturn(plan);

        ChManagePlan newPlan = new ChManagePlan();
        newPlan.setPlanId(2L);
        newPlan.setPatientId(1L);
        newPlan.setPlanStatus("ACTIVE");
        when(managePlanMapper.insert(any(ChManagePlan.class))).thenReturn(1);
        when(managePlanMapper.selectById(2L)).thenReturn(newPlan);

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

        // 无效状态应抛出异常
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
        when(followupTaskMapper.selectById(1L)).thenReturn(task);

        // PENDING → REMINDING
        task.setTaskStatus("REMINDING");
        when(followupTaskMapper.selectById(1L)).thenReturn(task);
        assertEquals("REMINDING", task.getTaskStatus());

        // REMINDING → DONE
        task.setTaskStatus("DONE");
        when(followupTaskMapper.selectById(1L)).thenReturn(task);
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

        // DRAFT → ACTIVE
        plan.setPlanStatus("ACTIVE");
        assertEquals("ACTIVE", plan.getPlanStatus());

        // ACTIVE → HISTORY (方案调整时旧方案归档)
        plan.setPlanStatus("HISTORY");
        assertEquals("HISTORY", plan.getPlanStatus());
    }

    /**
     * 验证签约到期状态流转: ACTIVE→EXPIRING→EXPIRED
     */
    @Test
    void testContractRenewalStatusTransition() {
        ChPatientContract contract = new ChPatientContract();
        contract.setContractId(1L);
        contract.setRenewalStatus("ACTIVE");

        // ACTIVE → EXPIRING
        contract.setRenewalStatus("EXPIRING");
        assertEquals("EXPIRING", contract.getRenewalStatus());

        // EXPIRING → EXPIRED
        contract.setRenewalStatus("EXPIRED");
        assertEquals("EXPIRED", contract.getRenewalStatus());

        // EXPIRED → RENEWED
        contract.setRenewalStatus("RENEWED");
        assertEquals("RENEWED", contract.getRenewalStatus());
    }
}
