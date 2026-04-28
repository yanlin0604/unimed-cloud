package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 医生分组成员关联表 ch_doctor_group_member
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_doctor_group_member")
public class ChDoctorGroupMember extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /**
     * 分组ID
     */
    private Long groupId;

    /**
     * 患者ID
     */
    private Long patientId;
}
