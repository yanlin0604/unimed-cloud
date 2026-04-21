package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDoctorTeamBo;
import org.dromara.chronic.domain.bo.ChDoctorTeamMemberBo;
import org.dromara.chronic.domain.vo.ChDoctorTeamMemberVo;
import org.dromara.chronic.domain.vo.ChDoctorTeamVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 医生团队服务
 *
 * @author unimed
 */
public interface IChDoctorTeamService {

    TableDataInfo<ChDoctorTeamVo> queryPageList(ChDoctorTeamBo bo, PageQuery pageQuery);

    Boolean insertByBo(ChDoctorTeamBo bo);

    Boolean updateByBo(ChDoctorTeamBo bo);

    Boolean deleteById(Long teamId);

    Boolean addMember(ChDoctorTeamMemberBo bo);

    Boolean removeMember(Long teamId, Long userId);

    List<ChDoctorTeamMemberVo> queryMembers(Long teamId);

    Boolean bindPatientTeam(Long patientId, Long teamId);
}
