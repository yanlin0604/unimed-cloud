package org.dromara.dhcore.domain.bo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 提交交付请求
 */
@Data
public class DhProductionSubmitBo {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 生成渠道
     */
    @NotBlank(message = "生成渠道不能为空")
    private String generationChannel;

    /**
     * 生成引用标识（如第三方任务ID）
     */
    private String generationRef;

    /**
     * 成片文件名
     */
    @NotBlank(message = "视频文件名不能为空")
    private String outputVideoName;

    /**
     * 成片视频地址
     */
    @NotBlank(message = "视频地址不能为空")
    private String outputVideoUrl;

    /**
     * 成片时长（秒）
     */
    @NotNull(message = "视频时长不能为空")
    private Integer outputVideoDurationSec;

    /**
     * 成片大小（MB）
     */
    @NotNull(message = "视频大小不能为空")
    private BigDecimal outputVideoSizeMb;

    /**
     * 操作人姓名
     */
    @NotBlank(message = "操作人不能为空")
    private String operatorName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 质检清单
     */
    @Valid
    @NotNull(message = "质检清单不能为空")
    private DhQcChecklistBo qcChecklist;
}
