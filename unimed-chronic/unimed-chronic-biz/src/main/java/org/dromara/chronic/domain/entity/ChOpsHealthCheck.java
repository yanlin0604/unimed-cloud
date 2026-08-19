package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 运维健康巡检对象 ch_ops_health_check
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ops_health_check")
public class ChOpsHealthCheck extends BaseEntity {

    /** 巡检ID */
    @TableId(value = "check_id")
    private Long checkId;

    /** 巡检批次号 */
    private String checkBatch;

    /** 目标组件(DB/REDIS/NACOS/MQ/HIS 等) */
    private String targetComponent;

    /** 检查结果(SUCCESS/FAILED/TIMEOUT) */
    private String checkStatus;

    /** 响应时长(毫秒) */
    private Long responseMs;

    /** 错误信息 */
    private String errorMsg;

    /** 是否触发告警 */
    private Boolean alertTriggered;

    /** 巡检时间 */
    private Date checkTime;
}
