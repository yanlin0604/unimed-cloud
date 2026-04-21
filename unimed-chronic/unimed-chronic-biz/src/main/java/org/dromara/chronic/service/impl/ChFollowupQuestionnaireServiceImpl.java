package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChFollowupAnswerBo;
import org.dromara.chronic.domain.bo.ChFollowupQuestionnaireBo;
import org.dromara.chronic.domain.entity.ChFollowupAnswer;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;
import org.dromara.chronic.domain.vo.ChFollowupAnswerVo;
import org.dromara.chronic.domain.vo.ChFollowupQuestionnaireVo;
import org.dromara.chronic.mapper.ChFollowupAnswerMapper;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.service.IChFollowupQuestionnaireService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 随访问卷服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChFollowupQuestionnaireServiceImpl implements IChFollowupQuestionnaireService {

    private final ChFollowupQuestionnaireMapper questionnaireMapper;
    private final ChFollowupAnswerMapper answerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuestionnaire(ChFollowupQuestionnaireBo bo) {
        validateSkipLogic(bo.getQuestions());
        ChFollowupQuestionnaire entity = MapstructUtils.convert(bo, ChFollowupQuestionnaire.class);
        if (entity.getVersion() == null) {
            entity.setVersion(1);
        }
        questionnaireMapper.insert(entity);
        return entity.getQuestionnaireId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateQuestionnaire(ChFollowupQuestionnaireBo bo) {
        ChFollowupQuestionnaire existing = questionnaireMapper.selectById(bo.getQuestionnaireId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("问卷模板不存在");
        }
        validateSkipLogic(bo.getQuestions());
        ChFollowupQuestionnaire entity = MapstructUtils.convert(bo, ChFollowupQuestionnaire.class);
        questionnaireMapper.updateById(entity);
        return null;
    }

    @Override
    public ChFollowupQuestionnaireVo queryById(Long questionnaireId) {
        return questionnaireMapper.selectVoById(questionnaireId);
    }

    @Override
    public TableDataInfo<ChFollowupQuestionnaireVo> queryPageList(ChFollowupQuestionnaireBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChFollowupQuestionnaire> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getQuestionnaireName()), ChFollowupQuestionnaire::getQuestionnaireName, bo.getQuestionnaireName());
        lqw.eq(StringUtils.isNotBlank(bo.getDiseaseCode()), ChFollowupQuestionnaire::getDiseaseCode, bo.getDiseaseCode());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsNationalStandard()), ChFollowupQuestionnaire::getIsNationalStandard, bo.getIsNationalStandard());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsActive()), ChFollowupQuestionnaire::getIsActive, bo.getIsActive());
        lqw.orderByDesc(ChFollowupQuestionnaire::getCreateTime);
        Page<ChFollowupQuestionnaireVo> page = questionnaireMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChFollowupQuestionnaireVo> queryByDiseaseCode(String diseaseCode) {
        return questionnaireMapper.selectVoList(
            Wrappers.<ChFollowupQuestionnaire>lambdaQuery()
                .eq(ChFollowupQuestionnaire::getDiseaseCode, diseaseCode)
                .eq(ChFollowupQuestionnaire::getIsActive, true)
                .orderByDesc(ChFollowupQuestionnaire::getVersion)
        );
    }

    @Override
    public Void toggleActive(Long questionnaireId, Boolean isActive) {
        ChFollowupQuestionnaire entity = questionnaireMapper.selectById(questionnaireId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("问卷模板不存在");
        }
        entity.setIsActive(isActive);
        questionnaireMapper.updateById(entity);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void submitAnswers(Long recordId, Long questionnaireId, List<ChFollowupAnswerBo> answers) {
        if (CollUtil.isEmpty(answers)) {
            return null;
        }
        List<ChFollowupAnswer> entities = new ArrayList<>(answers.size());
        for (ChFollowupAnswerBo bo : answers) {
            bo.setRecordId(recordId);
            bo.setQuestionnaireId(questionnaireId);
            entities.add(MapstructUtils.convert(bo, ChFollowupAnswer.class));
        }
        answerMapper.insertBatch(entities);
        return null;
    }

    @Override
    public List<ChFollowupAnswerVo> queryAnswersByRecordId(Long recordId) {
        return answerMapper.selectVoList(
            Wrappers.<ChFollowupAnswer>lambdaQuery()
                .eq(ChFollowupAnswer::getRecordId, recordId)
                .orderByAsc(ChFollowupAnswer::getCreateTime)
        );
    }

    /**
     * 校验 skip_logic JSON 结构合法性
     */
    private void validateSkipLogic(String questionsJson) {
        if (StringUtils.isBlank(questionsJson)) {
            throw new ServiceException("问卷题目不能为空");
        }
        try {
            JSONUtil.parseArray(questionsJson);
        } catch (Exception e) {
            try {
                JSONUtil.parseObj(questionsJson);
            } catch (Exception e2) {
                throw new ServiceException("questions JSON 格式不合法");
            }
        }
    }
}
