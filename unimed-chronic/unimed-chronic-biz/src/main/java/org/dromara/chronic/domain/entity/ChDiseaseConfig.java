package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 慢病病种配置对象 ch_disease_config
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_disease_config")
public class ChDiseaseConfig extends TenantEntity {

    @TableId(value = "config_id")
    private Long configId;

    private String diseaseCode;

    private String diseaseName;

    private String diseaseCategory;

    private Boolean isPrimary;

    private String parentDiseaseCode;

    private Long followupTemplateId;

    private Long assessmentStrategyId;

    /**
     * 监测项目 JSON
     */
    private String monitorItems;

    private Boolean isActive;

    @TableLogic
    private String delFlag;
}
