package org.dromara.chronic.service;

import org.dromara.chronic.domain.entity.ChDoctorCustomGroup;

import java.util.List;

/**
 * 医生自定义管理分组 Service 接口
 *
 * @author unimed
 */
public interface ICustomGroupService {

    /**
     * 查询医生的所有自定义分组
     */
    List<ChDoctorCustomGroup> listByDoctorId(Long doctorId);

    /**
     * 创建自定义分组
     */
    ChDoctorCustomGroup createGroup(String groupName, String description, Long doctorId);

    /**
     * 更新自定义分组
     */
    void updateGroup(Long groupId, String groupName, String description, Long doctorId);

    /**
     * 删除自定义分组（含清理成员关联）
     */
    void deleteGroup(Long groupId, Long doctorId);

    /**
     * 批量向分组添加患者（防重）
     */
    void addPatientsToGroup(Long groupId, List<Long> patientIds, Long doctorId);

    /**
     * 批量从分组移除患者
     */
    void removePatientsFromGroup(Long groupId, List<Long> patientIds, Long doctorId);
}
