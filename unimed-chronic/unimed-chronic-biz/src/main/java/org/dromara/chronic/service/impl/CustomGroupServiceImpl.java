package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChDoctorCustomGroup;
import org.dromara.chronic.domain.entity.ChDoctorGroupMember;
import org.dromara.chronic.mapper.ChDoctorCustomGroupMapper;
import org.dromara.chronic.mapper.ChDoctorGroupMemberMapper;
import org.dromara.chronic.service.ICustomGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生自定义管理分组 Service 实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomGroupServiceImpl implements ICustomGroupService {

    private final ChDoctorCustomGroupMapper groupMapper;
    private final ChDoctorGroupMemberMapper memberMapper;

    @Override
    public List<ChDoctorCustomGroup> listByDoctorId(Long doctorId) {
        return groupMapper.selectList(
            new LambdaQueryWrapper<ChDoctorCustomGroup>()
                .eq(ChDoctorCustomGroup::getDoctorId, doctorId)
                .orderByDesc(ChDoctorCustomGroup::getCreateTime));
    }

    @Override
    public ChDoctorCustomGroup createGroup(String groupName, String description, Long doctorId) {
        ChDoctorCustomGroup group = new ChDoctorCustomGroup();
        group.setGroupName(groupName);
        group.setDescription(description);
        group.setDoctorId(doctorId);
        groupMapper.insert(group);
        return group;
    }

    @Override
    public void updateGroup(Long groupId, String groupName, String description, Long doctorId) {
        ChDoctorCustomGroup group = groupMapper.selectById(groupId);
        if (group == null || !group.getDoctorId().equals(doctorId)) {
            log.warn("分组不存在或无权修改: groupId={}, doctorId={}", groupId, doctorId);
            return;
        }
        group.setGroupName(groupName);
        group.setDescription(description);
        groupMapper.updateById(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(Long groupId, Long doctorId) {
        ChDoctorCustomGroup group = groupMapper.selectById(groupId);
        if (group == null || !group.getDoctorId().equals(doctorId)) {
            log.warn("分组不存在或无权删除: groupId={}, doctorId={}", groupId, doctorId);
            return;
        }
        // 先清理组成员
        memberMapper.delete(new LambdaQueryWrapper<ChDoctorGroupMember>()
            .eq(ChDoctorGroupMember::getGroupId, groupId));
        // 再删除组本身
        groupMapper.deleteById(groupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPatientsToGroup(Long groupId, List<Long> patientIds, Long doctorId) {
        ChDoctorCustomGroup group = groupMapper.selectById(groupId);
        if (group == null || !group.getDoctorId().equals(doctorId)) {
            log.warn("分组不存在或无权操作: groupId={}, doctorId={}", groupId, doctorId);
            return;
        }

        // 查询现有成员，防重
        List<ChDoctorGroupMember> existMembers = memberMapper.selectList(
            new LambdaQueryWrapper<ChDoctorGroupMember>()
                .eq(ChDoctorGroupMember::getGroupId, groupId));
        List<Long> existPatientIds = existMembers.stream()
            .map(ChDoctorGroupMember::getPatientId)
            .collect(Collectors.toList());

        List<ChDoctorGroupMember> newMembers = patientIds.stream()
            .filter(pid -> !existPatientIds.contains(pid))
            .map(pid -> {
                ChDoctorGroupMember member = new ChDoctorGroupMember();
                member.setGroupId(groupId);
                member.setPatientId(pid);
                return member;
            }).collect(Collectors.toList());

        if (!newMembers.isEmpty()) {
            memberMapper.insertBatch(newMembers);
        }
    }

    @Override
    public void removePatientsFromGroup(Long groupId, List<Long> patientIds, Long doctorId) {
        ChDoctorCustomGroup group = groupMapper.selectById(groupId);
        if (group == null || !group.getDoctorId().equals(doctorId) || patientIds.isEmpty()) {
            return;
        }
        memberMapper.delete(new LambdaQueryWrapper<ChDoctorGroupMember>()
            .eq(ChDoctorGroupMember::getGroupId, groupId)
            .in(ChDoctorGroupMember::getPatientId, patientIds));
    }
}
