package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChDeviceRawRecord;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备原始数据视图对象
 *
 * @author unimed
 */
@Schema(description = "设备原始记录视图对象")
@Data
@AutoMapper(target = ChDeviceRawRecord.class)
public class ChDeviceRawRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "设备ID")
    private String deviceId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "原始数据")
    private String rawData;
    @Schema(description = "解析时间")
    private Date parsedAt;
    @Schema(description = "创建时间")
    private Date createTime;
}
