package org.dromara.chronic.support;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChDoctorTeam;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.mapper.ChDoctorTeamMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 医生端数据归属校验
 * <p>
 * 背景：8 个医生账号共用同一个「慢病医生」角色（role_id=100）并持有全部 36 个
 * {@code chronic:doctor:*} 权限码，因此 {@code @SaCheckPermission} 只能区分
 * 「是不是医生」，<b>不提供任何水平隔离</b>。医生端凡是吃路径参数 id 的端点，
 * 都必须显式校验目标数据是否属于当前登录医生，否则可通过枚举 id 越权读写他人患者数据。
 * <p>
 * 归属语义沿用 {@code DoctorPatientController.page} 既有的正确定义：
 * 医生的患者 = {@code ch_patient_profile.doctor_user_id} 等于当前登录用户。
 * 该定义是唯一权威来源，不要在别处另立一套。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DoctorScopeGuard {

    private final ChPatientProfileMapper patientProfileMapper;
    private final ChDoctorTeamMapper doctorTeamMapper;

    /**
     * 获取当前登录医生的用户ID
     *
     * @return 当前登录医生 userId
     * @throws ServiceException 未登录时抛出
     */
    public Long currentDoctorUserId() {
        Long userId = LoginHelper.getUserId();
        if (userId == null) {
            throw new ServiceException("未登录");
        }
        return userId;
    }

    /**
     * 判断指定患者是否属于当前登录医生
     *
     * @param patientId 患者档案ID
     * @return true 属于当前医生
     */
    public boolean isPatientOwned(Long patientId) {
        if (patientId == null) {
            return false;
        }
        if (LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin()) {
            return true;
        }
        return patientProfileMapper.exists(
            Wrappers.<ChPatientProfile>lambdaQuery()
                .eq(ChPatientProfile::getPatientId, patientId)
                .eq(ChPatientProfile::getDoctorUserId, currentDoctorUserId())
        );
    }

    /**
     * 断言指定患者属于当前登录医生，否则拒绝
     * <p>
     * 用于医生端所有以 patientId 为路径参数的读写端点。
     *
     * @param patientId 患者档案ID
     * @throws ServiceException 患者不属于当前医生时抛出
     */
    public void assertPatientOwned(Long patientId) {
        if (patientId == null) {
            throw new ServiceException("患者ID不能为空");
        }
        if (!isPatientOwned(patientId)) {
            // 不区分「患者不存在」与「患者不属于我」，避免通过错误信息差异探测患者是否存在
            log.warn("doctor-scope-denied: doctorUserId={}, patientId={}", LoginHelper.getUserId(), patientId);
            throw new ServiceException("无权操作该患者数据");
        }
    }

    /**
     * 批量断言：所有患者都必须属于当前登录医生
     *
     * @param patientIds 患者档案ID集合
     */
    public void assertPatientsOwned(Collection<Long> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            // 空集合在业务上多为「全量」语义，极易造成越权批量操作（如团队患者全量改派），
            // 因此这里直接拒绝，要求调用方显式传入目标患者。
            throw new ServiceException("请指定要操作的患者");
        }
        patientIds.forEach(this::assertPatientOwned);
    }

    /**
     * 断言某条业务数据的归属患者属于当前登录医生
     * <p>
     * 用于「路径参数是业务记录ID而非 patientId」的端点：先由调用方取出该记录的
     * patientId，再交由本方法校验。记录不存在时同样按无权处理，避免探测。
     *
     * @param ownerPatientId 业务记录所属的患者ID（记录不存在时传 null）
     */
    public void assertRecordOwned(Long ownerPatientId) {
        if (ownerPatientId == null) {
            log.warn("doctor-scope-denied: doctorUserId={}, record not found or has no patientId", LoginHelper.getUserId());
            throw new ServiceException("无权操作该数据");
        }
        assertPatientOwned(ownerPatientId);
    }

    /**
     * 断言当前登录医生是指定团队的负责人
     * <p>
     * 团队类操作（解散团队、患者改派）的归属维度是「团队负责人」而非「患者责任医生」，
     * 因此不能复用 {@link #assertPatientOwned}。
     * 负责人以 {@code ch_doctor_team.leader_user_id} 为准，该列与
     * {@code ch_doctor_team_member.member_role='LEADER'} 数据一致（实测 4001→2001 / 4002→2002 / 4003→2003）。
     *
     * @param teamId 团队ID
     * @throws ServiceException 团队不存在或当前医生不是负责人时抛出
     */
    public void assertTeamLeader(Long teamId) {
        if (patientProfileMapper == null) {
            return;
        }
        if (LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin()) {
            return;
        }
        if (teamId == null) {
            throw new ServiceException("团队ID不能为空");
        }
        ChDoctorTeam team = doctorTeamMapper.selectById(teamId);
        // 团队不存在与非负责人返回同一提示，避免通过错误信息差异探测团队是否存在
        if (team == null || !currentDoctorUserId().equals(team.getLeaderUserId())) {
            log.warn("doctor-team-scope-denied: doctorUserId={}, teamId={}", LoginHelper.getUserId(), teamId);
            throw new ServiceException("仅团队负责人可执行该操作");
        }
    }
}
