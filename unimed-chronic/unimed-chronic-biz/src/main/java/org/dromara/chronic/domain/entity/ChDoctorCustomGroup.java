package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 医生自定义管理分组表 ch_doctor_custom_group
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_doctor_custom_group")
public class ChDoctorCustomGroup extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 创建/所属医生ID
     */
    private Long doctorId;

    /**
     * 分组描述
     */
    private String description;
}
