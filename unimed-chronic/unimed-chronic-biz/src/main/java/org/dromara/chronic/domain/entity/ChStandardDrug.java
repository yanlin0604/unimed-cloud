package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 国家标准药品库对象 ch_standard_drug
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_standard_drug")
public class ChStandardDrug extends TenantEntity {

    @TableId(value = "drug_id")
    private Long drugId;

    private String drugCode;

    private String nationalCode;

    private String commonName;

    private String tradeName;

    private String specification;

    private String dosageForm;

    private String manufacturer;

    private String medicareCategory;

    private String chronicCategory;

    private BigDecimal refPrice;

    private String status;

    private String delFlag;
}
