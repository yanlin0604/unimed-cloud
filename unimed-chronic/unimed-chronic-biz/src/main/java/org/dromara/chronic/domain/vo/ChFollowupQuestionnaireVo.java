package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 随访问卷模板视图对象
 *
 * @author unimed
 */
@Schema(description = "随访问卷视图对象")
@Data
@AutoMapper(target = ChFollowupQuestionnaire.class)
public class ChFollowupQuestionnaireVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "问卷ID")
    private Long questionnaireId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "问卷名称")
    private String questionnaireName;
    @Schema(description = "版本")
    private Integer version;
    @Schema(description = "问题配置")
    private String questions;
    @Schema(description = "是否国家标准")
    private Boolean isNationalStandard;
    @Schema(description = "是否启用")
    private Boolean isActive;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "病种名称")
    private String diseaseName;
}
