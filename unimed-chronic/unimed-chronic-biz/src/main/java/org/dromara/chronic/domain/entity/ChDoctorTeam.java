package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 医生团队对象 ch_doctor_team
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_doctor_team")
public class ChDoctorTeam extends TenantEntity {

    @TableId(value = "team_id")
    private Long teamId;

    private String teamName;

    private Long deptId;

    private Long leaderUserId;

    private String teamStatus;

    @TableLogic
    private String delFlag;
}
