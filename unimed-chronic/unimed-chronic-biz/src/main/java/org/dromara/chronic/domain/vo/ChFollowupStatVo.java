package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 随访多维统计视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访多维统计视图对象")
public class ChFollowupStatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "随访总览统计")
    private Overview overview;

    @Schema(description = "随访趋势统计列表")
    private List<TrendItem> trendList;

    @Schema(description = "随访方式分布列表")
    private List<TypeDistributionItem> typeDistribution;

    @Schema(description = "执行人工作量与完成率排行")
    private List<AssigneeRankItem> assigneeRanking;

    @Schema(description = "病种随访统计")
    private List<DiseaseStatItem> diseaseStats;

    @Data
    @Schema(description = "随访总览指标")
    public static class Overview implements Serializable {
        @Schema(description = "总随访任务数")
        private Long totalCount = 0L;

        @Schema(description = "今日待办随访数")
        private Long todayPendingCount = 0L;

        @Schema(description = "今日完成随访数")
        private Long todayDoneCount = 0L;

        @Schema(description = "累计已完成数")
        private Long doneCount = 0L;

        @Schema(description = "任务池未认领数")
        private Long unassignedCount = 0L;

        @Schema(description = "逾期任务数")
        private Long overdueCount = 0L;

        @Schema(description = "总体随访完成率(%)")
        private BigDecimal completionRate = BigDecimal.ZERO;

        @Schema(description = "随访控制良好/达标率(%)")
        private BigDecimal controlledRate = BigDecimal.ZERO;
    }

    @Data
    @Schema(description = "随访趋势项")
    public static class TrendItem implements Serializable {
        @Schema(description = "统计日期 (yyyy-MM-dd)")
        private String date;

        @Schema(description = "计划随访数")
        private Long plannedCount = 0L;

        @Schema(description = "实际完成数")
        private Long doneCount = 0L;

        @Schema(description = "逾期数")
        private Long overdueCount = 0L;

        @Schema(description = "完成率(%)")
        private BigDecimal completionRate = BigDecimal.ZERO;
    }

    @Data
    @Schema(description = "随访方式分布项")
    public static class TypeDistributionItem implements Serializable {
        @Schema(description = "随访方式编码 (ONLINE/OFFLINE/PHONE)")
        private String visitType;

        @Schema(description = "随访方式名称")
        private String visitTypeName;

        @Schema(description = "数量")
        private Long count = 0L;

        @Schema(description = "占比(%)")
        private BigDecimal percentage = BigDecimal.ZERO;
    }

    @Data
    @Schema(description = "执行人工作量排行项")
    public static class AssigneeRankItem implements Serializable {
        @Schema(description = "执行人用户ID")
        private Long assigneeUserId;

        @Schema(description = "执行人姓名/昵称")
        private String assigneeNickName;

        @Schema(description = "分配任务总数")
        private Long totalTasks = 0L;

        @Schema(description = "已完成任务数")
        private Long doneTasks = 0L;

        @Schema(description = "待办任务数")
        private Long pendingTasks = 0L;

        @Schema(description = "逾期任务数")
        private Long overdueTasks = 0L;

        @Schema(description = "完成率(%)")
        private BigDecimal completionRate = BigDecimal.ZERO;
    }

    @Data
    @Schema(description = "病种随访统计项")
    public static class DiseaseStatItem implements Serializable {
        @Schema(description = "病种编码")
        private String diseaseCode;

        @Schema(description = "病种名称")
        private String diseaseName;

        @Schema(description = "随访任务总数")
        private Long totalCount = 0L;

        @Schema(description = "已完成数")
        private Long doneCount = 0L;

        @Schema(description = "控制良好数")
        private Long controlledCount = 0L;

        @Schema(description = "完成率(%)")
        private BigDecimal completionRate = BigDecimal.ZERO;

        @Schema(description = "控制良好率(%)")
        private BigDecimal controlledRate = BigDecimal.ZERO;
    }
}
