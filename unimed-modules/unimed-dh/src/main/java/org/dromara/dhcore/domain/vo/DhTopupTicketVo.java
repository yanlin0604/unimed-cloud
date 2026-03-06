package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值工单视图对象
 */
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
     * 用户名
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
