package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChDeviceBind;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备绑定视图对象
 *
 * @author unimed
 */
@Schema(description = "设备绑定视图对象")
@Data
@AutoMapper(target = ChDeviceBind.class)
public class ChDeviceBindVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "绑定ID")
    private Long bindId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "设备ID")
    private String deviceId;
    @Schema(description = "设备类型")
    private String deviceType;
    @Schema(description = "电池电量")
    private Integer batteryLevel;
    @Schema(description = "在线状态")
    private String onlineStatus;
    @Schema(description = "最后通信时间")
    private Date lastCommTime;
    @Schema(description = "创建时间")
    private Date createTime;
}
