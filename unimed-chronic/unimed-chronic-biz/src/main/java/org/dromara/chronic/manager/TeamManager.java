package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChDoctorTeam;
import org.dromara.chronic.mapper.ChDoctorTeamMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 团队管理编排层
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class TeamManager {

    private final ChDoctorTeamMapper teamMapper;
    private final ChPatientContractMapper patientContractMapper;

    @Transactional(rollbackFor = Exception.class)
    public boolean dissolveTeam(Long teamId) {
        ChDoctorTeam team = teamMapper.selectById(teamId);
        if (team == null) {
            return false;
        }
        team.setTeamStatus("DISSOLVED");
        return teamMapper.updateById(team) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean reassignPatients(Long teamId, Long targetTeamId, List<Long> patientIds) {
        var update = Wrappers.lambdaUpdate(org.dromara.chronic.domain.entity.ChPatientContract.class)
            .eq(org.dromara.chronic.domain.entity.ChPatientContract::getTeamId, teamId)
            .set(org.dromara.chronic.domain.entity.ChPatientContract::getTeamId, targetTeamId);
        if (patientIds != null && !patientIds.isEmpty()) {
            update.in(org.dromara.chronic.domain.entity.ChPatientContract::getPatientId, patientIds);
        }
        return patientContractMapper.update(null, update) > 0;
    }
}
