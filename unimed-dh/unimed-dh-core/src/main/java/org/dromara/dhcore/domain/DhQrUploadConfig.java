package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播收款码配置对象 dh_qr_upload_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_qr_upload_config")
public class DhQrUploadConfig extends TenantEntity {

    /**
     * 收款码配置ID
     */
    @TableId(value = "config_id")
    private Long configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 收款码类型
     */
    private String type;

    /**
     * 收款码图片ID列表
     */
    private String qrImageIds;

    /**
     * 收款账户名
     */
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
