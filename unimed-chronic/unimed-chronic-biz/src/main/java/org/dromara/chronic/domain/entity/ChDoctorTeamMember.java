package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 医生团队成员对象 ch_doctor_team_member
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_doctor_team_member")
public class ChDoctorTeamMember extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long teamId;

    private Long userId;

    private String memberRole;

    @TableLogic
    private String delFlag;
}
