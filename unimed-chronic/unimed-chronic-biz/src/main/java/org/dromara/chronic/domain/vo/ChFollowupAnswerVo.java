package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFollowupAnswer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 随访问卷作答视图对象
 *
 * @author unimed
 */
@Schema(description = "问卷答案视图对象")
@Data
@AutoMapper(target = ChFollowupAnswer.class)
public class ChFollowupAnswerVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "记录ID")
    private Long recordId;
    @Schema(description = "问卷ID")
    private Long questionnaireId;
    @Schema(description = "问题ID")
    private String questionId;
    @Schema(description = "答案值")
    private String answerValue;
    @Schema(description = "创建时间")
    private Date createTime;
}
