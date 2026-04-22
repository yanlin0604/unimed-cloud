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
 * 危险因子明细对象 ch_risk_factor_item
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_risk_factor_item")
public class ChRiskFactorItem extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long assessmentId;

    private String factorName;

    private String factorValue;

    private BigDecimal factorWeight;

    @TableLogic
    private String delFlag;
}