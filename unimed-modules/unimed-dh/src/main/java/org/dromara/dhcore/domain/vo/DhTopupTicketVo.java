package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值工单视图对�? */
@Data
public class DhTopupTicketVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 充值工单ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户�?     */
    private String userName;

    /**
     * 申请充值金�?     */
    private BigDecimal amount;

    /**
     * 工单状�?     */
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
     * 审核�?     */
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
