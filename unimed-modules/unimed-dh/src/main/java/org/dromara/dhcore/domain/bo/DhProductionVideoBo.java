package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 上传成品元数据请�? */
@Data
public class DhProductionVideoBo {

    /**
     * 成片文件�?     */
    @NotBlank(message = "视频文件名不能为�?)
    private String outputVideoName;

    /**
     * 成片视频地址
     */
    @NotBlank(message = "视频地址不能为空")
    private String outputVideoUrl;

    /**
     * 成片时长（秒�?     */
    @NotNull(message = "视频时长不能为空")
    private Integer outputVideoDurationSec;

    /**
     * 成片大小（MB�?     */
    @NotNull(message = "视频大小不能为空")
    private BigDecimal outputVideoSizeMb;

    /**
     * 操作人姓�?     */
    @NotBlank(message = "操作人不能为�?)
    private String operatorName;
}
