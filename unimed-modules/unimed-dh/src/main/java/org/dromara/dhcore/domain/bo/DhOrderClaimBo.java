package org.dromara.dhcore.domain.bo;

import lombok.Data;

/**
 * 订单领取请求
 */
@Data
public class DhOrderClaimBo {

    /**
     * 处理人姓名
     */
    private String operatorName;
}
