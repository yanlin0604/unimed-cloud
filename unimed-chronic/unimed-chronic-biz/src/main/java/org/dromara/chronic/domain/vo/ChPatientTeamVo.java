package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 当前患者签约医生团队 */
@Data
@Schema(description = "当前患者签约医生团队")
public class ChPatientTeamVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long teamId;
    private String teamName;
    private Long deptId;
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
    private String deptName;
    private Long leaderUserId;
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "leaderUserId")
    private String leaderNickName;
    private String teamStatus;
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "teamStatus", other = ChronicDictTypeConstant.CHRONIC_TEAM_STATUS)
    private String teamStatusName;
    private List<ChDoctorTeamMemberVo> members;
}
