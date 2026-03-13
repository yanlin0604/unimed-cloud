package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * C端充值记录视图对象（对应 DhTopupTicket，精简版）
 *
 * @author AI
 */
@Data
public class PortalTopupRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    private Long id;

    /**
     * 申请充值金额
     */
    private BigDecimal amount;

    /**
     * 实际到账金额（审核后填入）
     */
    private BigDecimal actualAmount;

    /**
     * 工单状态（PENDING/APPROVED/NEED_MORE/REJECTED）
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
     * 申请时间
     */
    private Date createTime;

    /**
     * 审核时间
     */
    private Date approvedAt;

    /**
     * 备注
     */
    private String remark;

    /**
     * 驳回原因
     */
    private String rejectReason;
}
