package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 收款码配置视图对�? */
@Data
public class DhQrUploadConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 收款码类�?     */
    private String type;

    /**
     * 收款码图片ID列表
     */
    private String qrImageIds;

    /**
     * 收款账户�?     */
    private String accountName;

    /**
     * 收款账号
     */
    private String accountNo;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 状�?     */
    private String status;

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
