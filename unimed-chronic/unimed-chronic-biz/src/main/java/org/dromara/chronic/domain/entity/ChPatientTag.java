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
     * 标签类型
     */
    private String tagType;

    /**
     * 标签值
     */
    private String tagValue;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
