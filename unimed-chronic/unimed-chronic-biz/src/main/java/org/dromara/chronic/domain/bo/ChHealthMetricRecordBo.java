package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 健康指标记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "健康指标记录业务对象")
@AutoMapper(target = ChHealthMetricRecord.class, reverseConvertGenerate = false)
public class ChHealthMetricRecordBo extends BaseEntity {

    @Schema(description = "指标记录ID")
    private Long metricId;

    @Schema(description = "患者ID（写入接口由控制器从 Token 或 PathVariable 注入，无需前端传）")
    private Long patientId;

    @Schema(description = "指标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "指标类型不能为空")
    private String metricType;

    @Schema(description = "指标值（简单数字字符串；血压拆为 BP_SYSTOLIC + BP_DIASTOLIC 两条独立记录）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "指标值不能为空")
    private String metricValue;

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

    /**
     * 数据来源: MANUAL/DEVICE/HIS_LIS
     */
    @Schema(description = "数据来源: MANUAL/DEVICE/HIS_LIS")
    private String dataSource;
}
