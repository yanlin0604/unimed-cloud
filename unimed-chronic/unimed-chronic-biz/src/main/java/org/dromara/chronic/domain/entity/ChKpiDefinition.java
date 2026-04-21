package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * KPI指标定义对象 ch_kpi_definition
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_kpi_definition")
public class ChKpiDefinition extends TenantEntity {

    @TableId(value = "kpi_id")
    private Long kpiId;

    private String kpiCode;

    private String kpiName;

    /**
     * KPI公式
     */
    private String kpiFormula;

    /**
     * KPI类别: MANAGEMENT_RATE/COMPLIANCE_RATE/CONTROL_RATE
     */
    private String kpiCategory;

    @TableLogic
    private String delFlag;
}
