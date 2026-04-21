package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 病种关系对象 ch_disease_relation
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_disease_relation")
public class ChDiseaseRelation extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String parentDiseaseCode;

    private String complicationDiseaseCode;

    private String relationType;

    private Boolean isActive;

    @TableLogic
    private String delFlag;
}
