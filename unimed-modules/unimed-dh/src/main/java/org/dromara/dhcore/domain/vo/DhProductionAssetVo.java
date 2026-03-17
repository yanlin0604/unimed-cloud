package org.dromara.dhcore.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.dhcore.domain.DhOrderProductionAsset;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单生产资产视图对象
 */
@Data
@AutoMapper(target = DhOrderProductionAsset.class)
public class DhProductionAssetVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 生成渠道
     */
    private String generationChannel;

    /**
     * 生成引用标识（如第三方任务ID�?     */
    private String generationRef;

    /**
     * 成片文件�?     */
    private String outputVideoName;

    /**
     * 成片视频地址
     */
    private String outputVideoUrl;

    /**
     * 成片时长（秒�?     */
    private Integer outputVideoDurationSec;

    /**
     * 成片大小（MB�?     */
    private BigDecimal outputVideoSizeMb;

    /**
     * 操作人姓�?     */
    private String operatorName;

    /**
     * 提交时间
     */
    private Date submittedAt;

    /**
     * 备注
     */
    private String remark;
}
