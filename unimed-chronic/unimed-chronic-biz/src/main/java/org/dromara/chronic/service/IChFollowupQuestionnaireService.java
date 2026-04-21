package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChFollowupAnswerBo;
import org.dromara.chronic.domain.bo.ChFollowupQuestionnaireBo;
import org.dromara.chronic.domain.vo.ChFollowupAnswerVo;
import org.dromara.chronic.domain.vo.ChFollowupQuestionnaireVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 随访问卷服务
 *
 * @author unimed
 */
public interface IChFollowupQuestionnaireService {

    Long createQuestionnaire(ChFollowupQuestionnaireBo bo);

    Void updateQuestionnaire(ChFollowupQuestionnaireBo bo);

    ChFollowupQuestionnaireVo queryById(Long questionnaireId);

    TableDataInfo<ChFollowupQuestionnaireVo> queryPageList(ChFollowupQuestionnaireBo bo, PageQuery pageQuery);

    List<ChFollowupQuestionnaireVo> queryByDiseaseCode(String diseaseCode);

    Void toggleActive(Long questionnaireId, Boolean isActive);

    Void submitAnswers(Long recordId, Long questionnaireId, List<ChFollowupAnswerBo> answers);

    List<ChFollowupAnswerVo> queryAnswersByRecordId(Long recordId);
}
