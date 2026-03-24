package org.dromara.dhcore.domain.bo.portal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * C端补充凭证业务对象
 *
 * @author unimed
 */
@Data
public class PortalTopupSupplementBo {

    /**
     * 补充的凭证截图 OSS ID 列表
     */
    @NotNull(message = "凭证不能为空")
    @Size(min = 1, message = "至少上传一张凭证")
    private List<String> voucherOssIds;

    /**
     * 补充说明
     */
    @Size(max = 500, message = "补充说明不能超过500个字符")
    private String voucherDesc;
}
