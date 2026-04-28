package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 管理路径进度聚合视图对象
 *
 * @author unimed
 */
@Schema(description = "管理路径进度聚合视图对象")
@Data
public class PathwayProgressVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "病种编码")
    private String diseaseCode;

    @Schema(description = "当前阶段")
    private String currentStage;

    @Schema(description = "是否逾期")
    private Boolean isOverdue;

    @Schema(description = "阶段列表")
    private List<StageInfo> stages;

    /**
     * 单个阶段信息
     */
    @Schema(description = "阶段信息")
    @Data
    public static class StageInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Schema(description = "阶段编码")
        private String stageCode;

        @Schema(description = "阶段名称")
        private String stageName;

        @Schema(description = "阶段状态 (COMPLETED/IN_PROGRESS/PENDING)")
        private String status;

        @Schema(description = "完成时间")
        private Date completedTime;

        @Schema(description = "截止日期")
        private Date dueDate;
    }
}
