package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 随访任务自动跑批分发执行参数
 *
 * @author unimed
 */
@Data
@Schema(description = "随访任务自动跑批分发执行参数")
public class ChFollowupDispatchRunBo {

    @Schema(description = "分发策略：RANDOM(加权随机), LEAST_LOADED(最少待办负载均衡), ROUND_ROBIN(轮询分发), DISEASE_MATCH(专病优先加权)")
    private String strategy = "RANDOM";

    @Schema(description = "本次最大分发任务数量，默认 100")
    private Integer limit = 100;

    @Schema(description = "限定病种编码（选填，不填则分发所有待分发任务）")
    private String diseaseCode;

    @Schema(description = "限定随访方式（选填）")
    private String visitType;
}
