package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFollowupPlan;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 随访计划视图对象
 *
 * @author unimed
 */
@Schema(description = "随访计划视图对象")
@Data
@AutoMapper(target = ChFollowupPlan.class)
public class ChFollowupPlanVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "周期天数")
    private Integer cycleDays;
    @Schema(description = "总轮次")
    private Integer totalRounds;
    @Schema(description = "方案状态")
    private String planStatus;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "子项列表")
    private List<ChFollowupPlanItemVo> itemList;
}
