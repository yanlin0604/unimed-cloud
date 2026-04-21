package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * ICD 字典对象 ch_icd_dict
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_icd_dict")
public class ChIcdDict extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String icdCode;

    private String icdVersion;

    private String icdNameCn;

    private String icdNameEn;

    private String category;

    @TableLogic
    private String delFlag;
}
