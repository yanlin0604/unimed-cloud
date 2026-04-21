package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;

import java.util.List;

/**
 * 患者账号服务
 *
 * @author unimed
 */
public interface IChPatientAccountService {

    Long register(ChPatientAccountBo bo);

    ChPatientAccountVo queryByPhone(String phone);

    ChPatientAccountVo queryByOpenid(String openid);

    ChPatientAccountVo queryByPatientId(Long patientId);

    List<ChPatientAccountVo> queryFamilyProxies(Long masterAccountId);

    Boolean bindFamilyProxy(ChPatientAccountBo bo);

    Boolean unbindFamilyProxy(Long accountId);

    Boolean updateAuthScope(Long accountId, String authScope);
}
