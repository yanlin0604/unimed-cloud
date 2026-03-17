package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收款码配置提交对�? */
@Data
public class DhQrUploadConfigBo {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    /**
     * 收款码类�?     */
    @NotBlank(message = "收款码类型不能为�?)
    private String type;

    /**
     * 收款码图片ID列表
     */
    private String qrImageIds;

    /**
     * 收款账户�?     */
    @NotBlank(message = "收款账户名不能为�?)
    private String accountName;

    /**
     * 收款账号
     */
    @NotBlank(message = "收款账号不能为空")
    private String accountNo;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 状�?     */
    @NotBlank(message = "状态不能为�?)
    private String status;

    /**
     * 备注
     */
    private String remark;
}
