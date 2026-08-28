package org.dromara.chronic.support.rule;

import org.dromara.chronic.domain.entity.ChFollowupRule;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.mapper.ChFollowupRuleMapper;
import org.dromara.chronic.support.rule.FollowupRuleEngine.FollowupPlanProposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 随访规则与路径推导引擎单元测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class FollowupRuleEngineTest {

    private ChFollowupQuestionnaireMapper questionnaireMapper;
    private ChFollowupRuleMapper followupRuleMapper;
    private FollowupRuleEngine ruleEngine;

    @BeforeEach
    void setUp() {
        questionnaireMapper = mock(ChFollowupQuestionnaireMapper.class);
        followupRuleMapper = mock(ChFollowupRuleMapper.class);
        // 默认模拟表无数据: 引擎应完全回退代码内置默认,行为与改造前一致
        org.mockito.Mockito.when(followupRuleMapper.selectList(org.mockito.ArgumentMatchers.any()))
            .thenReturn(java.util.Collections.emptyList());
        ruleEngine = new FollowupRuleEngine(questionnaireMapper, followupRuleMapper);
    }

    @Test
    @DisplayName("推导高血压一级/低危方案：每90天1次，每年4次")
    void testHypertensionLowRisk() {
        FollowupPlanProposal proposal = ruleEngine.generateProposal("HTN", "LOW");
        assertEquals("HTN", proposal.diseaseCode());
        assertEquals("LOW", proposal.managementLevel());
        assertEquals(90, proposal.cycleDays());
        assertEquals(4, proposal.totalRounds());
        assertEquals(7, proposal.firstDueDays());
        assertTrue(proposal.summaryAdvice().contains("一级管理"));
    }

    @Test
    @DisplayName("推导高血压二级/中危方案：每60天1次，每年6次")
    void testHypertensionMediumRisk() {
        FollowupPlanProposal proposal = ruleEngine.generateProposal("HTN", "MEDIUM");
        assertEquals(60, proposal.cycleDays());
        assertEquals(6, proposal.totalRounds());
        assertTrue(proposal.summaryAdvice().contains("二级管理"));
    }

    @Test
    @DisplayName("推导高血压三级/高危极高危方案：每30天1次，每年12次")
    void testHypertensionHighRisk() {
        FollowupPlanProposal proposal = ruleEngine.generateProposal("HTN", "HIGH");
        assertEquals(30, proposal.cycleDays());
        assertEquals(12, proposal.totalRounds());
        assertTrue(proposal.summaryAdvice().contains("三级管理"));
    }

    @Test
    @DisplayName("推导糖尿病常规管理方案：每90天1次，每年4次")
    void testDiabetesControlled() {
        FollowupPlanProposal proposal = ruleEngine.generateProposal("T2DM", "LOW");
        assertEquals("T2DM", proposal.diseaseCode());
        assertEquals(90, proposal.cycleDays());
        assertEquals(4, proposal.totalRounds());
        assertTrue(proposal.summaryAdvice().contains("常规管理"));
    }

    @Test
    @DisplayName("推导糖尿病强化管理方案：每30天1次，每年12次")
    void testDiabetesUncontrolled() {
        FollowupPlanProposal proposal = ruleEngine.generateProposal("T2DM", "HIGH");
        assertEquals(30, proposal.cycleDays());
        assertEquals(12, proposal.totalRounds());
        assertTrue(proposal.summaryAdvice().contains("强化管理"));
    }

    @Test
    @DisplayName("推导慢阻肺(COPD)管理方案：每年4次，面对面≥2次")
    void testCopdProposal() {
        FollowupPlanProposal proposal = ruleEngine.generateProposal("COPD", "LOW");
        assertEquals("COPD", proposal.diseaseCode());
        assertEquals(90, proposal.cycleDays());
        assertEquals(4, proposal.totalRounds());
        assertTrue(proposal.summaryAdvice().contains("慢阻肺"));
    }

    @Test
    @DisplayName("精确规则优先于病种通配规则")
    void testExactRuleHasPriority() {
        ChFollowupRule exact = rule("HTN", "HIGH", 45, 8, "精确规则");
        ChFollowupRule wildcard = rule("HTN", "ANY", 90, 4, "通配规则");
        org.mockito.Mockito.when(followupRuleMapper.selectList(org.mockito.ArgumentMatchers.any()))
            .thenReturn(List.of(wildcard, exact));

        FollowupPlanProposal proposal = ruleEngine.generateProposal("htn", "high");

        assertEquals(45, proposal.cycleDays());
        assertEquals(8, proposal.totalRounds());
        assertEquals("精确规则", proposal.summaryAdvice());
    }

    @Test
    @DisplayName("病种ANY优先于GENERAL等级规则")
    void testDiseaseAnyHasPriorityOverGeneralLevel() {
        ChFollowupRule diseaseAny = rule("COPD", "ANY", 45, 8, "病种通配");
        ChFollowupRule generalHigh = rule("GENERAL", "HIGH", 30, 12, "通用高危");
        org.mockito.Mockito.when(followupRuleMapper.selectList(org.mockito.ArgumentMatchers.any()))
            .thenReturn(List.of(generalHigh, diseaseAny));

        FollowupPlanProposal proposal = ruleEngine.generateProposal("COPD", "HIGH");

        assertEquals(45, proposal.cycleDays());
        assertEquals("病种通配", proposal.summaryAdvice());
    }

    @Test
    @DisplayName("无病种规则时回退GENERAL等级规则")
    void testGeneralLevelFallback() {
        ChFollowupRule generalHigh = rule("GENERAL", "HIGH", 21, 10, "通用高危配置");
        org.mockito.Mockito.when(followupRuleMapper.selectList(org.mockito.ArgumentMatchers.any()))
            .thenReturn(List.of(generalHigh));

        FollowupPlanProposal proposal = ruleEngine.generateProposal("UNKNOWN", "HIGH");

        assertEquals(21, proposal.cycleDays());
        assertEquals(10, proposal.totalRounds());
        assertEquals("通用高危配置", proposal.summaryAdvice());
    }

    @Test
    @DisplayName("停用规则被跳过并继续使用内置默认")
    void testInactiveRuleSkipped() {
        ChFollowupRule inactive = rule("HTN", "HIGH", 1, 1, "停用规则");
        inactive.setIsActive(false);
        org.mockito.Mockito.when(followupRuleMapper.selectList(org.mockito.ArgumentMatchers.any()))
            .thenReturn(List.of(inactive));

        FollowupPlanProposal proposal = ruleEngine.generateProposal("HTN", "HIGH");

        assertEquals(30, proposal.cycleDays());
        assertEquals(12, proposal.totalRounds());
    }

    private ChFollowupRule rule(String diseaseCode, String riskLevel, int cycleDays,
                                int totalRounds, String advice) {
        ChFollowupRule rule = new ChFollowupRule();
        rule.setDiseaseCode(diseaseCode);
        rule.setRiskLevel(riskLevel);
        rule.setCycleDays(cycleDays);
        rule.setTotalRounds(totalRounds);
        rule.setFirstDueDays(7);
        rule.setDefaultVisitType("PHONE");
        rule.setSummaryAdvice(advice);
        rule.setIsActive(true);
        return rule;
    }
}
