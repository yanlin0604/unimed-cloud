package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 随访任务自动跑批分发执行结果视图
 *
 * @author unimed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "随访任务自动跑批分发执行结果视图")
public class ChFollowupDispatchResultVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "本次扫描到的任务池待分发总数")
    private Integer totalPendingTasks;

    @Schema(description = "本次成功分配任务数")
    private Integer dispatchedCount;

    @Schema(description = "本次跳过/因配额已满未能分发的任务数")
    private Integer skippedCount;

    @Schema(description = "参与本次分发的有效执行人数")
    private Integer activeAssigneesCount;

    @Schema(description = "使用的分发策略")
    private String strategy;

    @Schema(description = "各执行人分得任务数明细 (执行人姓名 -> 分配任务数)")
    private Map<String, Integer> assigneeDispatchedMap;

    @Schema(description = "分发处理摘要说明")
    private String message;
}
