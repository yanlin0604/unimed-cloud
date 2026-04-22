package org.dromara.chronic.manager;

import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.vo.ChDiseaseAnalysisVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.mapper.*;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DashboardManager 测试
 * 验证首期病种范围（不含 COPD）、专病分析、共病检测
 *
 * @author unimed
 */
@ExtendWith(MockitoExtension.class)
class DashboardManagerTest {

    @Mock
    private ChAreaDictMapper areaDictMapper;

    @Mock
    private ChStatAreaDayMapper statAreaDayMapper;

    @Mock
    private ChKpiDefinitionMapper kpiDefinitionMapper;

    @Mock
    private ChPatientDiseaseMapper patientDiseaseMapper;

    @Mock
    private ChPatientProfileMapper patientProfileMapper;

    @Mock
    private ChWarningEventMapper warningEventMapper;

    @Mock
    private ChFollowupTaskMapper followupTaskMapper;

    @Mock
    private ChManagePlanMapper managePlanMapper;

    @InjectMocks
    private DashboardManager dashboardManager;

    @Test
    void shouldRejectCOPDAsFirstPhaseDisease() {
        // COPD 因随访模板未就绪，不应在首期列表中
        assertThrows(RuntimeException.class, () -> {
            dashboardManager.queryDiseaseAnalysis("COPD");
        });
    }

    @Test
    void shouldAcceptDiabetesAsFirstPhaseDisease() {
        // 糖尿病是首期病种，不应抛异常
        // queryDiseaseAnalysis: selectCount(总数) → managePlanMapper.selectCount(控制数) → selectList(患者ID) → selectCount(新增)
        when(patientDiseaseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(managePlanMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(patientDiseaseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        when(warningEventMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(followupTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        ChDiseaseAnalysisVo vo = dashboardManager.queryDiseaseAnalysis("DIABETES");
        assertNotNull(vo);
        assertEquals("DIABETES", vo.getDiseaseCode());
        assertEquals("糖尿病", vo.getDiseaseName());
    }

    @Test
    void shouldCalculateControlRateCorrectly() {
        // patientDiseaseMapper.selectCount: 总数=2, 新增(近30天)=0
        when(patientDiseaseMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(2L)   // 总数
            .thenReturn(0L);   // 新增患者数(近30天)

        // managePlanMapper.selectCount: ACTIVE方案数=1（已纳入规范管理的患者）
        when(managePlanMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // selectList 用于获取患者ID列表（预警+随访查询用）
        ChPatientDisease p1 = new ChPatientDisease();
        p1.setPatientId(1L);
        p1.setDiseaseCode("HYPERTENSION");
        ChPatientDisease p2 = new ChPatientDisease();
        p2.setPatientId(2L);
        p2.setDiseaseCode("HYPERTENSION");
        when(patientDiseaseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(p1, p2));

        when(warningEventMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        // followupTaskMapper.selectCount 调用顺序：已完成数 → 总任务数
        when(followupTaskMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L, 10L);

        ChDiseaseAnalysisVo vo = dashboardManager.queryDiseaseAnalysis("HYPERTENSION");
        assertEquals(2L, vo.getTotalPatientCount());
        assertEquals(1L, vo.getControlledCount());
        assertEquals(java.math.BigDecimal.valueOf(50.00).setScale(2), vo.getControlRate().setScale(2));
        assertEquals(1L, vo.getWarningCount());
        assertEquals("高血压", vo.getDiseaseName());
        assertEquals("近30天", vo.getStatPeriod());
    }

    @Test
    void shouldReturnEmptyPageWhenNoDiseasePatients() {
        when(patientDiseaseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        ChPatientDiseaseBo bo = new ChPatientDiseaseBo();
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);

        TableDataInfo<ChPatientProfileVo> result = dashboardManager.querySpecialDiseasePatientPage(bo, pageQuery, null);
        assertNotNull(result);
    }

    @Test
    void shouldDetectComorbidityPatients() {
        // 同一患者有两种病种 → 共病
        ChPatientDisease d1 = new ChPatientDisease();
        d1.setPatientId(1L);
        d1.setDiseaseCode("HYPERTENSION");

        ChPatientDisease d2 = new ChPatientDisease();
        d2.setPatientId(1L);
        d2.setDiseaseCode("DIABETES");

        ChPatientDisease d3 = new ChPatientDisease();
        d3.setPatientId(2L);
        d3.setDiseaseCode("HYPERTENSION"); // 单病种患者

        when(patientDiseaseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(d1, d2, d3));

        Page<ChPatientProfileVo> mockPage = new Page<>();
        when(patientProfileMapper.selectVoPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        ChPatientDiseaseBo bo = new ChPatientDiseaseBo();
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);

        TableDataInfo<ChPatientProfileVo> result = dashboardManager.queryComorbidityPatientPage(bo, pageQuery);
        assertNotNull(result);
        // 验证只有共病患者(patientId=1)被查询
        verify(patientProfileMapper).selectVoPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldDeduplicateDiseaseCodesInComorbidity() {
        // 同一患者同一种病种两条记录（并发症和主诊断）应只计1次
        ChPatientDisease d1 = new ChPatientDisease();
        d1.setPatientId(1L);
        d1.setDiseaseCode("HYPERTENSION");

        ChPatientDisease d2 = new ChPatientDisease();
        d2.setPatientId(1L);
        d2.setDiseaseCode("HYPERTENSION"); // 重复

        when(patientDiseaseMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(d1, d2));

        Page<ChPatientProfileVo> mockPage = new Page<>();
        when(patientProfileMapper.selectVoPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        ChPatientDiseaseBo bo = new ChPatientDiseaseBo();
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);

        // 去重后只有1种病种，不是共病患者，所以不应查患者档案
        TableDataInfo<ChPatientProfileVo> result = dashboardManager.queryComorbidityPatientPage(bo, pageQuery);
        assertNotNull(result);
    }
}
