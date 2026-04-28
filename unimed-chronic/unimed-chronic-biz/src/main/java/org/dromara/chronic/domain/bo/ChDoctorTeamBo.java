package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDoctorTeam;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 医生团队业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "医生团队业务对象")
@AutoMapper(target = ChDoctorTeam.class, reverseConvertGenerate = false)
public class ChDoctorTeamBo extends BaseEntity {

    @Schema(description = "团队ID")
    private Long teamId;

    @Schema(description = "团队名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "团队名称不能为空")
    private String teamName;
    @Schema(description = "科室ID")
    private Long deptId;

    @Schema(description = "负责人用户ID")
    private Long leaderUserId;

    @Schema(description = "团队状态")
    private String teamStatus;
}
