package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDoctorTeamMember;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 医生团队成员业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "医生团队成员业务对象")
@AutoMapper(target = ChDoctorTeamMember.class, reverseConvertGenerate = false)
public class ChDoctorTeamMemberBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "团队ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "团队ID不能为空")
    private Long teamId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "成员角色")
    private String memberRole;
}
