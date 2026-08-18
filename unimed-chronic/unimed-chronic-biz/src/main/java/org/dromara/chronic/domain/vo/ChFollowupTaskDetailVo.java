package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 随访任务详情
 *
 * @author unimed
 */
@Data
@Schema(description = "随访任务详情")
public class ChFollowupTaskDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChFollowupTaskVo task;

    private ChFollowupQuestionnaireVo questionnaire;

    private ChFollowupRecordVo record;

    private List<ChFollowupAnswerVo> answers;
}
