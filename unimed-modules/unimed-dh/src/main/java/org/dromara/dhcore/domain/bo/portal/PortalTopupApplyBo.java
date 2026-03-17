package org.dromara.dhcore.domain.bo.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * C端充值申请业务对�?
 *
 * @author unimed
 */
@Data
public class PortalTopupApplyBo {

    /**
     * 充值金�?
     */
    @NotNull(message = "充值金额不能为�?)
    private BigDecimal amount;

    /**
     * 支付方式（WECHAT/ALIPAY/BANK_CARD�?
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentType;

    /**
     * 凭证截图 OSS ID 列表
     */
    @NotNull(message = "请上传支付凭�?)
    @Size(min = 1, message = "至少上传一张支付凭�?)
    private List<String> voucherOssIds;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字�?)
    private String remark;
}
