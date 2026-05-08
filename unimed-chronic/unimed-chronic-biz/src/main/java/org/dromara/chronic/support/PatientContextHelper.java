package org.dromara.chronic.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Component;

/**
 * 患者端上下文辅助类
 * 用于正确获取当前登录患者的 patientId
 * <p>
 * 核心问题：LoginHelper.getUserId() 返回的是 accountId（账号ID），而非 patientId（患者档案ID）
 * 本辅助类通过查询 ch_patient_account 表获取正确的 patientId
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientContextHelper {

    private final IChPatientAccountService patientAccountService;

    /**
     * 获取当前登录患者的 patientId（必须有患者档案）
     * <p>
     * 适用场景：需要操作患者业务数据（如指标录入、报告查询等）
     *
     * @return 患者ID
     * @throws ServiceException 如果账号不存在或未关联患者档案
     */
    public Long getCurrentPatientId() {
        Long accountId = LoginHelper.getUserId();
        if (accountId == null) {
            throw new ServiceException("未登录");
        }

        ChPatientAccountVo account = patientAccountService.getAccountById(accountId);
        if (account == null) {
            throw new ServiceException("账号不存在");
        }

        Long patientId = account.getPatientId();
        if (patientId == null) {
            log.warn("账号 {} 未关联患者档案", accountId);
            throw new ServiceException("您还没有健康档案，请联系医院机构建立档案后使用完整功能");
        }

        return patientId;
    }

    /**
     * 获取当前登录患者的 patientId（允许为空）
     * <p>
     * 适用场景：仅查询账号信息，不强制要求有患者档案
     *
     * @return 患者ID，如果未关联则返回 null
     */
    public Long getCurrentPatientIdOrNull() {
        Long accountId = LoginHelper.getUserId();
        if (accountId == null) {
            return null;
        }

        ChPatientAccountVo account = patientAccountService.getAccountById(accountId);
        if (account == null) {
            return null;
        }

        return account.getPatientId();
    }

    /**
     * 获取当前登录的 accountId（账号ID）
     *
     * @return 账号ID
     */
    public Long getCurrentAccountId() {
        return LoginHelper.getUserId();
    }
}
