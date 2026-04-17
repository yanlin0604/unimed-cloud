package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * C端作品下载信息视图对象
 *
 * @author unimed
 */
@Data
public class PortalOrderDownloadVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 视频下载地址（归一化后的相对路径）
     */
    private String downloadUrl;

    /**
     * 视频文件名
     */
    private String fileName;

    /**
     * 文件大小（MB）
     */
    private BigDecimal fileSizeMb;

    /**
     * 视频时长（秒）
     */
    private Integer durationSec;
}
