package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言采集录音记录查询 BO（B端）
 *
 * @author unimed
 */
@Data
@Schema(description = "录音记录查询参数")
public class DhDialectRecordQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 方言名称
     */
    @Schema(description = "方言名称")
    private String dialectName;

    /**
     * 审核状态 PENDING/APPROVED/REJECTED
     */
    @Schema(description = "审核状态")
    private String auditStatus;

    /**
     * 提交开始时间
     */
    @Schema(description = "提交开始时间")
    private String beginTime;

    /**
     * 提交结束时间
     */
    @Schema(description = "提交结束时间")
    private String endTime;

    /**
     * 邀请码来源
     */
    @Schema(description = "邀请码")
    private String inviteCode;
}
