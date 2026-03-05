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
 * 数字人口播订单生产资产对象 dh_order_production_asset
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_order_production_asset")
public class DhOrderProductionAsset extends TenantEntity {

    /**
     * 资产ID
     */
    @TableId(value = "asset_id")
    private Long assetId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 生成渠道
     */
    private String generationChannel;

    /**
     * 生成引用标识（如第三方任务ID）
     */
    private String generationRef;

    /**
     * 成片文件名
     */
    private String outputVideoName;

    /**
     * 成片视频地址
     */
    private String outputVideoUrl;

    /**
     * 成片时长（秒）
     */
    private Integer outputVideoDurationSec;

    /**
     * 成片大小（MB）
     */
    private BigDecimal outputVideoSizeMb;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 提交时间
     */
    private Date submittedAt;

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
