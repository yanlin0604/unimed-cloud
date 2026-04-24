package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * 随访计划项视图对象
 *
 * @author unimed
 */
@Schema(description = "随访计划项视图对象")
@Data
@AutoMapper(target = ChFollowupPlanItem.class)
public class ChFollowupPlanItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "项类型")
    private String itemType;
    @Schema(description = "项配置")
    private String itemConfig;

    @Schema(description = "随访项类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "itemType", other = ChronicDictTypeConstant.CHRONIC_FOLLOWUP_ITEM_TYPE)
    private String itemTypeName;
}
