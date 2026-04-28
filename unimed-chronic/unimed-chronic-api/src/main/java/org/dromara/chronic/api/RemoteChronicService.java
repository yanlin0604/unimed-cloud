package org.dromara.chronic.api;

import java.util.Map;

/**
 * 慢病管理远程服务
 * <p>
 * 对外 Dubbo 读服务接口，供统计大屏/其他业务服务调用
 * 写操作通过 REST API + 鉴权，不暴露 Dubbo 写接口
 *
 * @author unimed
 */
public interface RemoteChronicService {

    /**
     * 查询患者摘要信息
     *
     * @param patientId 患者ID
     * @return 患者摘要（name/idCard/manageStatus/riskLevel/diseaseCode等）
     */
    Map<String, Object> getPatientSummary(Long patientId);

    /**
     * 查询患者风险等级
     *
     * @param patientId 患者ID
     * @return 风险等级(LOW/MEDIUM/HIGH/VERY_HIGH)
     */
    String getPatientRiskLevel(Long patientId);

    /**
     * 查询患者随访状态
     *
     * @param patientId 患者ID
     * @return 随访状态摘要（pendingCount/doneCount/overdueCount）
     */
    Map<String, Long> getPatientFollowupStatus(Long patientId);

    /**
     * 查询部门下活跃预警数量
     *
     * @param deptId 部门ID
     * @return 活跃预警数量
     */
    Long getActiveWarningCount(Long deptId);

    /**
     * 查询部门管理患者数
     *
     * @param deptId 部门ID
     * @return 管理中患者数
     */
    Long getManagedPatientCount(Long deptId);
}
