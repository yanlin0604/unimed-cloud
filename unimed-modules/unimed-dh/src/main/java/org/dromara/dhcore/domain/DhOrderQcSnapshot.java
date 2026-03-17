package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播订单质检快照对象 dh_order_qc_snapshot
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_order_qc_snapshot")
public class DhOrderQcSnapshot extends TenantEntity {

    /**
     * 质检快照ID
     */
    @TableId(value = "qc_id")
    private Long qcId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 口型同步（0否 1是）
     */
    private Integer lipSync;

    /**
     * 画面无瑕疵（0否 1是）
     */
    private Integer noVisualDefect;

    /**
     * 文案匹配（0否 1是）
     */
    private Integer scriptMatched;

    /**
     * 时长达标（0否 1是）
     */
    private Integer durationOk;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
