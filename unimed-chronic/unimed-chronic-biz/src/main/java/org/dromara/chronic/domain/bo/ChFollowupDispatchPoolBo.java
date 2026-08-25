package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.chronic.domain.entity.ChFollowupDispatchPool;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.List;

/**
 * 随访任务自动分发人员池业务对象
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "随访任务自动分发人员池业务对象")
@AutoMapper(target = ChFollowupDispatchPool.class, reverseConvertGenerate = false)
public class ChFollowupDispatchPoolBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "执行人用户ID")
    private Long userId;

    @Schema(description = "批量执行人用户ID列表（用于批量导入）")
    private List<Long> userIds;

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "执行人姓名")
    private String nickName;

    @Schema(description = "联系电话")
    private String phonenumber;

    @Schema(description = "适用病种编码（逗号分隔，*表示全部）")
    private String diseaseCodes;

    @Schema(description = "适用随访方式（逗号分隔，*表示全部）")
    private String visitTypes;

    @Schema(description = "当前待办任务上限")
    private Integer maxPendingTasks;

    @Schema(description = "分发权重(1-100)")
    private Integer weight;

    @Schema(description = "是否启用接单(1启用 0暂停)")
    private Boolean isActive;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "机构ID")
    private Long orgId;
}
