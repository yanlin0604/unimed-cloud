package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChDoctorTeamMember;

import java.io.Serial;
import java.io.Serializable;

/**
 * 医生团队成员视图对象
 *
 * @author unimed
 */
@Schema(description = "团队成员视图对象")
@Data
@AutoMapper(target = ChDoctorTeamMember.class)
public class ChDoctorTeamMemberVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "团队ID")
    private Long teamId;
    @Schema(description = "团队名称")
    private String teamName;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名称")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "成员角色")
    private String memberRole;
    @Schema(description = "成员角色名称")
    private String memberRoleName;
}
