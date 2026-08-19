package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChDoctorTeam;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 医生团队视图对象
 *
 * @author unimed
 */
@Schema(description = "医生团队视图对象")
@Data
@AutoMapper(target = ChDoctorTeam.class)
public class ChDoctorTeamVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "团队ID")
    private Long teamId;
    @Schema(description = "团队名称")
    private String teamName;
    @Schema(description = "机构ID")
    private Long orgId;
    @Schema(description = "科室ID")
    private Long deptId;
    @Schema(description = "负责人用户ID")
    private Long leaderUserId;
    @Schema(description = "团队状态")
    private String teamStatus;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "团队状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "teamStatus", other = ChronicDictTypeConstant.CHRONIC_TEAM_STATUS)
    private String teamStatusName;
    @Schema(description = "部门名称")
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
    private String deptName;

    @Schema(description = "团队负责人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "leaderUserId")
    private String leaderNickName;
}
