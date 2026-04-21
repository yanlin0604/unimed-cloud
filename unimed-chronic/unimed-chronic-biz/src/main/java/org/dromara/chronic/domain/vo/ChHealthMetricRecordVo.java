package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 健康指标记录视图对象
 *
 * @author unimed
 */
@Schema(description = "健康指标记录视图对象")
@Data
@AutoMapper(target = ChHealthMetricRecord.class)
public class ChHealthMetricRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "指标记录ID")
    private Long metricId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "指标类型")
    private String metricType;
    @Schema(description = "指标值")
    private BigDecimal metricValue;
    @Schema(description = "单位")
    private String unit;
    @Schema(description = "测量场景")
    private String measureScene;
    @Schema(description = "测量时段")
    private String measurePeriod;
    @Schema(description = "测量体位")
    private String measurePosture;
    @Schema(description = "参考值下限")
    private BigDecimal referenceValueMin;
    @Schema(description = "参考值上限")
    private BigDecimal referenceValueMax;
    @Schema(description = "是否异常")
    private Boolean isAbnormal;
    @Schema(description = "数据来源")
    private String dataSource;
    @Schema(description = "创建时间")
    private Date createTime;
}
