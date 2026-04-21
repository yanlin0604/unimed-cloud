package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChHealthExamItem;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 体检检验项业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "体检检验项业务对象")
@AutoMapper(target = ChHealthExamItem.class, reverseConvertGenerate = false)
public class ChHealthExamItemBo extends BaseEntity {

    @Schema(description = "体检ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "体检ID不能为空")
    private Long examId;

    @Schema(description = "检验项名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "检验项名称不能为空")
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
