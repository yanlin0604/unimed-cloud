package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFollowupDispatchPool;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 随访任务自动分发人员池视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访任务自动分发人员池视图对象")
@AutoMapper(target = ChFollowupDispatchPool.class)
public class ChFollowupDispatchPoolVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "执行人用户ID")
    private Long userId;

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "执行人姓名")
    private String nickName;

    @Schema(description = "联系电话")
    private String phonenumber;

    @Schema(description = "适用病种编码（逗号分隔，*表示全部）")
    private String diseaseCodes;

    @Schema(description = "适用专病名称列表（由数据库动态解析）")
    private java.util.List<String> diseaseNameList;

    @Schema(description = "适用随访方式（逗号分隔，*表示全部）")
    private String visitTypes;

    @Schema(description = "当前待办任务上限")
    private Integer maxPendingTasks;

    @Schema(description = "分发权重(1-100)")
    private Integer weight;

    @Schema(description = "是否启用接单(1启用 0暂停)")
    private Boolean isActive;

    @Schema(description = "当前名下待办随访任务数")
    private Integer currentPendingCount;

    @Schema(description = "历史累计已完成随访任务数")
    private Integer totalCompletedCount;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "创建时间")
    private Date createTime;
}
