package org.dromara.chronic.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.github.linpeilie.Converter;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChFollowupRuleBo;
import org.dromara.chronic.domain.entity.ChFollowupRule;
import org.dromara.chronic.mapper.ChFollowupRuleMapper;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 随访排期规则服务单元测试
 * <p>
 * 锁定逐轮生成模型下 total_rounds 的口径：规则只负责生成首轮，轮次由服务端固定为 1，
 * 不接受运营配置；同时锁定 BO 上不得再出现 totalRounds 必填注解 —— 该注解会在
 * Bean Validation 阶段（进入 service 之前）拒绝管理端请求，使 setTotalRounds(1) 永远来不及生效。
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class ChFollowupRuleServiceImplTest {

    private ChFollowupRuleMapper ruleMapper;
    private ChFollowupRuleServiceImpl service;

    @BeforeAll
    public static void setUpMapstructConverter() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("converter", new Converter());
        new SpringUtil().postProcessBeanFactory(beanFactory);
    }

    @BeforeEach
    public void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupRule.class);
        ruleMapper = mock(ChFollowupRuleMapper.class);
        when(ruleMapper.selectCount(any())).thenReturn(0L);
        service = new ChFollowupRuleServiceImpl(ruleMapper, mock(DiseaseNameHelper.class));
    }

    private ChFollowupRuleBo baseBo() {
        ChFollowupRuleBo bo = new ChFollowupRuleBo();
        bo.setDiseaseCode("HTN");
        bo.setRiskLevel("HIGH");
        bo.setCycleDays(30);
        bo.setIsActive(true);
        return bo;
    }

    private ChFollowupRule captureInserted() {
        ArgumentCaptor<ChFollowupRule> captor = ArgumentCaptor.forClass(ChFollowupRule.class);
        verify(ruleMapper).insert(captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("新增规则不传 totalRounds 也能成功，且落库轮次固定为 1")
    public void createRuleWithoutTotalRounds_persistsOne() {
        ChFollowupRuleBo bo = baseBo();
        assertNull(bo.getTotalRounds(), "前置条件：管理端表单已不再传该字段");

        assertDoesNotThrow(() -> service.createRule(bo));

        ChFollowupRule saved = captureInserted();
        assertEquals(1, saved.getTotalRounds(), "规则只生成首轮，轮次必须为 1");
        assertEquals("HTN", saved.getDiseaseCode());
        assertEquals("HIGH", saved.getRiskLevel());
        assertEquals(30, saved.getCycleDays());
    }

    @Test
    @DisplayName("旧调用方传 12 轮也被归一为 1，杜绝「配了多轮却只生成首轮」")
    public void createRuleWithLegacyMultiRounds_normalizedToOne() {
        ChFollowupRuleBo bo = baseBo();
        bo.setTotalRounds(12);

        service.createRule(bo);

        assertEquals(1, captureInserted().getTotalRounds());
    }

    @Test
    @DisplayName("编辑规则同样把轮次归一为 1")
    public void updateRule_normalizesTotalRoundsToOne() {
        ChFollowupRule existing = new ChFollowupRule();
        existing.setId(1001L);
        existing.setDiseaseCode("HTN");
        existing.setRiskLevel("HIGH");
        existing.setTotalRounds(12);
        when(ruleMapper.selectById(1001L)).thenReturn(existing);

        ChFollowupRuleBo bo = baseBo();
        bo.setId(1001L);
        bo.setTotalRounds(6);
        service.updateRule(bo);

        ArgumentCaptor<ChFollowupRule> captor = ArgumentCaptor.forClass(ChFollowupRule.class);
        verify(ruleMapper).updateById(captor.capture());
        assertEquals(1, captor.getValue().getTotalRounds());
    }

    @Test
    @DisplayName("回归护栏：totalRounds 不得带 @NotNull，否则不传该字段的请求会被参数校验拒绝")
    public void totalRoundsMustNotBeBeanValidationRequired() throws Exception {
        Field field = ChFollowupRuleBo.class.getDeclaredField("totalRounds");
        assertNull(field.getAnnotation(NotNull.class),
            "totalRounds 由服务端固定为 1，BO 上保留 @NotNull 会让管理端新增/编辑规则直接报「总轮次不能为空」");
    }

    @Test
    @DisplayName("随访周期非法仍被拒绝（未被本次改动放宽）")
    public void createRuleRejectsNonPositiveCycleDays() {
        ChFollowupRuleBo bo = baseBo();
        bo.setCycleDays(0);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.createRule(bo));
        assertTrue(ex.getMessage().contains("随访周期"), ex.getMessage());
    }

    @Test
    @DisplayName("风险等级非法被拒绝")
    public void createRuleRejectsIllegalRiskLevel() {
        ChFollowupRuleBo bo = baseBo();
        bo.setRiskLevel("CRITICAL");
        ServiceException ex = assertThrows(ServiceException.class, () -> service.createRule(bo));
        assertTrue(ex.getMessage().contains("风险等级"), ex.getMessage());
    }

    @Test
    @DisplayName("随访方式非法被拒绝")
    public void createRuleRejectsIllegalVisitType() {
        ChFollowupRuleBo bo = baseBo();
        bo.setDefaultVisitType("HOME_VISIT");
        ServiceException ex = assertThrows(ServiceException.class, () -> service.createRule(bo));
        assertTrue(ex.getMessage().contains("随访方式"), ex.getMessage());
    }

    @Test
    @DisplayName("同病种同等级重复建规则被拒绝（唯一键幂等）")
    public void createRuleRejectsDuplicateKey() {
        when(ruleMapper.selectCount(any())).thenReturn(1L);
        ServiceException ex = assertThrows(ServiceException.class, () -> service.createRule(baseBo()));
        assertTrue(ex.getMessage().contains("已存在规则"), ex.getMessage());
        verify(ruleMapper, never()).insert(any(ChFollowupRule.class));
    }

    @Test
    @DisplayName("首轮到期天数与默认方式缺省时补默认值")
    public void createRuleFillsDefaults() {
        service.createRule(baseBo());
        ChFollowupRule saved = captureInserted();
        assertNotNull(saved.getFirstDueDays());
        assertEquals(7, saved.getFirstDueDays());
        assertEquals("PHONE", saved.getDefaultVisitType());
    }
}
