package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChDrugInteraction;

import java.io.Serial;
import java.io.Serializable;

/**
 * 药物相互作用视图对象
 *
 * @author unimed
 */
@Schema(description = "药物相互作用视图对象")
@Data
@AutoMapper(target = ChDrugInteraction.class)
public class ChDrugInteractionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "药品A编码")
    private String drugCodeA;
    @Schema(description = "药品B编码")
    private String drugCodeB;
    @Schema(description = "相互作用级别")
    private String interactionLevel;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "临床建议")
    private String clinicalAdvice;
}
