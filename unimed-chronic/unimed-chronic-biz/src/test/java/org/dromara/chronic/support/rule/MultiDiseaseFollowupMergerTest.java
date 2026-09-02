package org.dromara.chronic.support.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.mapper.ChFollowupRuleMapper;
import org.dromara.chronic.support.rule.MultiDiseaseFollowupMerger.MergedProposal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 多病共管合并引擎单元测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class MultiDiseaseFollowupMergerTest {

    private MultiDiseaseFollowupMerger merger;

    @BeforeEach
    void setUp() {
        ChFollowupQuestionnaireMapper questionnaireMapper = mock(ChFollowupQuestionnaireMapper.class);
        ChFollowupRuleMapper followupRuleMapper = org.mockito.Mockito.mock(ChFollowupRuleMapper.class);
        org.mockito.Mockito.when(followupRuleMapper.selectList(org.mockito.ArgumentMatchers.any()))
            .thenReturn(java.util.Collections.emptyList());
        FollowupRuleEngine ruleEngine = new FollowupRuleEngine(questionnaireMapper, followupRuleMapper);
        merger = new MultiDiseaseFollowupMerger(ruleEngine, new ObjectMapper());
    }

    @Test
    @DisplayName("单病种：直接输出对应单病种方案")
    void testSingleDisease() {
        MergedProposal proposal = merger.mergeProposals(List.of("HTN"), Map.of("HTN", "LOW"));
        assertFalse(proposal.isMultiDisease());
        assertEquals("HTN", proposal.primaryDiseaseCode());
        assertEquals(90, proposal.cycleDays());
        assertEquals(1, proposal.totalRounds());
    }

    @Test
    @DisplayName("多病共管合并：高危高血压(30天) + 常规糖尿病(90天) 取严合并为30天周期")
    void testMultiDiseaseMergeStrictestCycle() {
        MergedProposal proposal = merger.mergeProposals(
            List.of("HTN", "T2DM"),
            Map.of("HTN", "HIGH", "T2DM", "LOW")
        );
        assertTrue(proposal.isMultiDisease());
        assertEquals(2, proposal.diseaseCodes().size());
        assertEquals(30, proposal.cycleDays()); // 取严格的高危高血压 30 天周期
        assertEquals(1, proposal.totalRounds());
        assertEquals("HIGH", proposal.managementLevel());
        assertTrue(proposal.summaryAdvice().contains("多病共管"));
    }
}
