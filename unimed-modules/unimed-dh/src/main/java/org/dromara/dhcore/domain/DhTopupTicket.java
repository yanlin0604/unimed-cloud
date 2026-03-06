package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 数字人口播充值工单对象 dh_topup_ticket
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_topup_ticket")
public class DhTopupTicket extends TenantEntity {

    /**
     * 充值工单ID
     */
    @TableId(value = "ticket_id")
    private Long ticketId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名快照
     */
    private String userName;

    /**
     * 申请充值金额
     */
    private BigDecimal amount;

    /**
     * 工单状态
     */
    private String status;

    /**
     * 凭证说明
     */
    private String voucherDesc;

    /**
     * 凭证图片ID列表
     */
    private String voucherImageIds;

    /**
     * 实际到账金额
     */
    private BigDecimal actualAmount;

    /**
     * 审核人
     */
    private String approvedBy;

    /**
     * 审核通过时间
     */
    private Date approvedAt;

    /**
     * 驳回原因
     */
    private String rejectReason;

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
