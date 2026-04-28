package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;

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
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.api.RemoteDictService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteDictDataVo;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @DubboReference
    private RemoteUserService remoteUserService;

    @DubboReference
    private RemoteDictService remoteDictService;

    @Override
    public TableDataInfo<ChDoctorTeamVo> queryPageList(ChDoctorTeamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChDoctorTeam> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTeamName()), ChDoctorTeam::getTeamName, bo.getTeamName());

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
        // 校验是否已存在该成员
        Long count = memberMapper.selectCount(
            Wrappers.<ChDoctorTeamMember>lambdaQuery()
                .eq(ChDoctorTeamMember::getTeamId, bo.getTeamId())
                .eq(ChDoctorTeamMember::getUserId, bo.getUserId())
        );
        if (count > 0) {
            throw new ServiceException("该用户已是团队成员，请勿重复添加");
        }

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
        List<ChDoctorTeamMemberVo> members = memberMapper.selectVoList(
            Wrappers.<ChDoctorTeamMember>lambdaQuery().eq(ChDoctorTeamMember::getTeamId, teamId)
        );

        if (members.isEmpty()) {
            return members;
        }

        // 查询团队名称
        try {
            ChDoctorTeam team = teamMapper.selectById(teamId);
            if (team != null) {
                members.forEach(member -> member.setTeamName(team.getTeamName()));
            }
        } catch (Exception e) {
            // 查询失败不影响主流程
        }

        // 收集用户ID
        List<Long> userIds = members.stream()
            .map(ChDoctorTeamMemberVo::getUserId)
            .filter(ObjectUtil::isNotNull)
            .distinct()
            .collect(Collectors.toList());

        if (!userIds.isEmpty()) {
            // 批量查询用户信息
            try {
                List<RemoteUserVo> users = remoteUserService.selectListByIds(userIds);
                Map<Long, RemoteUserVo> userMap = users.stream()
                    .collect(Collectors.toMap(RemoteUserVo::getUserId, u -> u, (u1, u2) -> u1));

                // 回填用户名称和昵称
                members.forEach(member -> {
                    RemoteUserVo user = userMap.get(member.getUserId());
                    if (user != null) {
                        member.setUserName(user.getUserName());
                        member.setNickName(user.getNickName());
                    }
                });
            } catch (Exception e) {
                // Dubbo调用失败不影响主流程
            }
        }

        // 查询成员角色字典
        try {
            List<RemoteDictDataVo> dictDataList = remoteDictService.selectDictDataByType(ChronicDictTypeConstant.DOCTOR_GROUP_TYPE);
            if (CollUtil.isNotEmpty(dictDataList)) {
                Map<String, String> dictMap = dictDataList.stream()
                    .collect(Collectors.toMap(RemoteDictDataVo::getDictValue, RemoteDictDataVo::getDictLabel, (v1, v2) -> v1));
                members.forEach(member -> {
                    String roleName = dictMap.get(member.getMemberRole());
                    if (StringUtils.isNotBlank(roleName)) {
                        member.setMemberRoleName(roleName);
                    }
                });
            }
        } catch (Exception e) {
            // Dubbo调用失败不影响主流程
        }

        return members;
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
