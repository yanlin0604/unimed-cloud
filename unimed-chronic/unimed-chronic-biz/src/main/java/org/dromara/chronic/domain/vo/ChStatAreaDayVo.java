package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChStatAreaDay;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 区域日统计视图对象
 *
 * @author unimed
 */
@Schema(description = "区域日统计视图对象")
@Data
@AutoMapper(target = ChStatAreaDay.class)
public class ChStatAreaDayVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "区域编码")
    private String areaCode;
    @Schema(description = "统计日期")
    private Date statDate;
    @Schema(description = "患者数")
    private Long patientCount;
    @Schema(description = "管理数")
    private Long managedCount;
    @Schema(description = "预警数")
    private Long warningCount;
    @Schema(description = "随访数")
    private Long followupCount;
}
