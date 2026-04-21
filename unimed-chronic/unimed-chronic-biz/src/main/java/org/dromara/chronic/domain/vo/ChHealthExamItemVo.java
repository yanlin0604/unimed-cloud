package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChHealthExamItem;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 体检检验项视图对象
 *
 * @author unimed
 */
@Schema(description = "体检检验项视图对象")
@Data
@AutoMapper(target = ChHealthExamItem.class)
public class ChHealthExamItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "体检ID")
    private Long examId;
    @Schema(description = "检验项名称")
    private String itemName;
    @Schema(description = "检验项编码")
    private String itemCode;
    @Schema(description = "结果值")
    private String resultValue;
    @Schema(description = "参考范围")
    private String referenceRange;
    @Schema(description = "是否异常")
    private Boolean isAbnormal;
    @Schema(description = "DR分级")
    private Integer drGrade;
    @Schema(description = "TCSS评分")
    private Integer tcssScore;
    @Schema(description = "MRS评分")
    private Integer mrsScore;
    @Schema(description = "NIHSS评分")
    private Integer nihssScore;
    @Schema(description = "eGFR值")
    private BigDecimal egfrValue;
}
