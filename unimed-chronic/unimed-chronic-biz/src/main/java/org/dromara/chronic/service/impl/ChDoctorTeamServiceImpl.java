package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDoctorTeamBo;
import org.dromara.chronic.domain.bo.ChDoctorTeamMemberBo;
import org.dromara.chronic.domain.entity.ChDoctorTeam;
import org.dromara.chronic.domain.entity.ChDoctorTeamMember;
import org.dromara.chronic.domain.vo.ChDoctorTeamMemberVo;
import org.dromara.chronic.domain.vo.ChDoctorTeamVo;
import org.dromara.chronic.mapper.ChDoctorTeamMapper;
import org.dromara.chronic.mapper.ChDoctorTeamMemberMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.dromara.chronic.service.IChDoctorTeamService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 医生团队服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChDoctorTeamServiceImpl implements IChDoctorTeamService {

    private final ChDoctorTeamMapper teamMapper;
    private final ChDoctorTeamMemberMapper memberMapper;
    private final ChPatientContractMapper patientContractMapper;

    @Override
    public TableDataInfo<ChDoctorTeamVo> queryPageList(ChDoctorTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChDoctorTeam> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTeamName()), ChDoctorTeam::getTeamName, bo.getTeamName());
        lqw.eq(ObjectUtil.isNotNull(bo.getOrgId()), ChDoctorTeam::getOrgId, bo.getOrgId());
        lqw.eq(ObjectUtil.isNotNull(bo.getDeptId()), ChDoctorTeam::getDeptId, bo.getDeptId());
        lqw.eq(StringUtils.isNotBlank(bo.getTeamStatus()), ChDoctorTeam::getTeamStatus, bo.getTeamStatus());
        lqw.orderByDesc(ChDoctorTeam::getCreateTime);
        Page<ChDoctorTeamVo> page = teamMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public Boolean insertByBo(ChDoctorTeamBo bo) {
        ChDoctorTeam entity = MapstructUtils.convert(bo, ChDoctorTeam.class);
        if (StringUtils.isBlank(entity.getTeamStatus())) {
            entity.setTeamStatus("ACTIVE");
        }
        return teamMapper.insert(entity) > 0;
    }

    @Override
    public Boolean updateByBo(ChDoctorTeamBo bo) {
        ChDoctorTeam entity = MapstructUtils.convert(bo, ChDoctorTeam.class);
        return teamMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean deleteById(Long teamId) {
        return teamMapper.deleteById(teamId) > 0;
    }

    @Override
    public Boolean addMember(ChDoctorTeamMemberBo bo) {
        ChDoctorTeamMember entity = MapstructUtils.convert(bo, ChDoctorTeamMember.class);
        if (StringUtils.isBlank(entity.getMemberRole())) {
            entity.setMemberRole("MEMBER");
        }
        return memberMapper.insert(entity) > 0;
    }

    @Override
    public Boolean removeMember(Long teamId, Long userId) {
        return memberMapper.delete(
            Wrappers.<ChDoctorTeamMember>lambdaQuery()
                .eq(ChDoctorTeamMember::getTeamId, teamId)
                .eq(ChDoctorTeamMember::getUserId, userId)
        ) > 0;
    }

    @Override
    public List<ChDoctorTeamMemberVo> queryMembers(Long teamId) {
        return memberMapper.selectVoList(Wrappers.<ChDoctorTeamMember>lambdaQuery().eq(ChDoctorTeamMember::getTeamId, teamId));
    }

    @Override
    public Boolean bindPatientTeam(Long patientId, Long teamId) {
        return patientContractMapper.update(null,
            Wrappers.lambdaUpdate(org.dromara.chronic.domain.entity.ChPatientContract.class)
                .eq(org.dromara.chronic.domain.entity.ChPatientContract::getPatientId, patientId)
                .set(org.dromara.chronic.domain.entity.ChPatientContract::getTeamId, teamId)
        ) > 0;
    }
}
