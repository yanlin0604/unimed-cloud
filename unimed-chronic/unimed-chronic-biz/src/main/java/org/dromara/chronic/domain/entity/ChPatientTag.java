package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 患者标签对象 ch_patient_tag
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_tag")
public class ChPatientTag extends TenantEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 标签大类（字典 chronic_tag_type）RISK/CUSTOM/COMORBIDITY
     */
    private String tagType;

    /**
     * 标签字典编码（ch_patient_tag_dict.tag_code）
     */
    private String tagCode;

    /**
     * 标签值/显示名称
     */
    private String tagValue;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
