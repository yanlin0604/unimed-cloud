package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 数字人口播会员配置对象 dh_member_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_member_config")
public class DhMemberConfig extends TenantEntity {

    /**
     * 会员配置ID
     */
    @TableId(value = "config_id")
    private Long configId;

    /**
     * 会员等级
     */
    private String level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 单价
     */
    private BigDecimal orderPrice;

    /**
     * 月度额度
     */
    private Integer monthlyLimit;

    /**
     * 速度优先级
     */
    private Integer speedPriority;

    /**
     * 最低充值要求
     */
    private BigDecimal minTopupAmount;

    /**
     * 有效期天数
     */
    private Integer validityDays;

    /**
     * 预计交付时长
     */
    private Integer expectDeliveryHours;

    /**
     * 重做次数上限
     */
    private Integer redoLimit;

    /**
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
