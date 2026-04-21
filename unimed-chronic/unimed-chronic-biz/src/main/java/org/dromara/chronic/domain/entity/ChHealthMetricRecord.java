package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 健康指标记录对象 ch_health_metric_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_health_metric_record")
public class ChHealthMetricRecord extends TenantEntity {

    @TableId(value = "metric_id")
    private Long metricId;

    private Long patientId;

    /**
     * 指标类型: BP_SYSTOLIC/BP_DIASTOLIC/BLOOD_GLUCOSE/HEART_RATE/SPO2/TEMPERATURE/ECG/WEIGHT/BMI/WAIST/LIPID/URIC_ACID
     */
    private String metricType;

    private BigDecimal metricValue;

    private String unit;

    private String measureScene;

    private String measurePeriod;

    private String measurePosture;

    /**
     * 参考值下限（年龄/性别自适应）
     */
    private BigDecimal referenceValueMin;

    /**
     * 参考值上限（年龄/性别自适应）
     */
    private BigDecimal referenceValueMax;

    private Boolean isAbnormal;

    /**
     * 数据来源: MANUAL/DEVICE/HIS_LIS
     */
    private String dataSource;

    @TableLogic
    private String delFlag;
}
