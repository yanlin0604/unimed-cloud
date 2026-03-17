package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 数字人口播订单处理日志对�?dh_order_process_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_order_process_log")
public class DhOrderProcessLog extends TenantEntity {

    /**
     * 日志ID
     */
    @TableId(value = "log_id")
    private Long logId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 操作说明
     */
    private String actionText;

    /**
     * 操作�?     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
