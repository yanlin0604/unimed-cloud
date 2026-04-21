package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 药物冲突检测结果
 *
 * @author unimed
 */
@Schema(description = "药物相互作用检查视图对象")
@Data
public class DrugInteractionCheckVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否存在冲突")
    private boolean conflict;

    @Schema(description = "已存在药品编码")
    private String existingDrugCode;

    @Schema(description = "目标药品编码")
    private String targetDrugCode;

    @Schema(description = "相互作用级别")
    private String interactionLevel;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "临床建议")
    private String clinicalAdvice;
}
