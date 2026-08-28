package org.dromara.chronic.service.impl;

import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.chronic.domain.vo.ChDoctorTeamVo;
import org.dromara.chronic.domain.vo.ChPatientTeamVo;
import org.dromara.chronic.mapper.ChDoctorTeamMapper;
import org.dromara.chronic.mapper.ChDoctorTeamMemberMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 当前患者签约团队查询测试。 */
@Tag("chronic-dev")
class ChDoctorTeamCurrentPatientTest {

    @Test
    void shouldReturnNullWhenPatientHasNoActiveContract() {
        Fixture fixture = fixture();
        when(fixture.contractMapper.selectOne(any())).thenReturn(null);

        assertNull(fixture.service.queryCurrentPatientTeam(1001L));
    }

    @Test
    void shouldReturnOnlyTeamLinkedByCurrentPatientContract() {
        Fixture fixture = fixture();
        ChPatientContract contract = new ChPatientContract();
        contract.setPatientId(1001L);
        contract.setTeamId(88L);
        contract.setContractStatus("ACTIVE");
        when(fixture.contractMapper.selectOne(any())).thenReturn(contract);

        ChDoctorTeamVo team = new ChDoctorTeamVo();
        team.setTeamId(88L);
        team.setTeamName("社区慢病管理团队");
        team.setDeptId(103L);
        team.setDeptName("全科医学科");
        team.setTeamStatus("ACTIVE");
        when(fixture.teamMapper.selectVoById(88L)).thenReturn(team);
        when(fixture.memberMapper.selectVoList(any())).thenReturn(List.of());

        ChPatientTeamVo result = fixture.service.queryCurrentPatientTeam(1001L);

        assertEquals(88L, result.getTeamId());
        assertEquals("社区慢病管理团队", result.getTeamName());
        assertEquals("全科医学科", result.getDeptName());
        assertTrue(result.getMembers().isEmpty());
    }

    @Test
    void shouldHideDissolvedTeam() {
        Fixture fixture = fixture();
        ChPatientContract contract = new ChPatientContract();
        contract.setTeamId(88L);
        when(fixture.contractMapper.selectOne(any())).thenReturn(contract);
        ChDoctorTeamVo team = new ChDoctorTeamVo();
        team.setTeamId(88L);
        team.setTeamStatus("DISSOLVED");
        when(fixture.teamMapper.selectVoById(88L)).thenReturn(team);

        assertNull(fixture.service.queryCurrentPatientTeam(1001L));
    }

    private static Fixture fixture() {
        ChDoctorTeamMapper teamMapper = mock(ChDoctorTeamMapper.class);
        ChDoctorTeamMemberMapper memberMapper = mock(ChDoctorTeamMemberMapper.class);
        ChPatientContractMapper contractMapper = mock(ChPatientContractMapper.class);
        ChDoctorTeamServiceImpl service = new ChDoctorTeamServiceImpl(teamMapper, memberMapper, contractMapper);
        return new Fixture(service, teamMapper, memberMapper, contractMapper);
    }

    private record Fixture(ChDoctorTeamServiceImpl service,
                           ChDoctorTeamMapper teamMapper,
                           ChDoctorTeamMemberMapper memberMapper,
                           ChPatientContractMapper contractMapper) {
    }
}
