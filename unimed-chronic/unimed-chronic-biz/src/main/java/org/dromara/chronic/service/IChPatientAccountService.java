package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.bo.WxLoginCodeBo;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.domain.vo.WxLoginVo;

import java.util.List;

/**
 * 患者账号服务
 *
 * @author unimed
 */
public interface IChPatientAccountService {

    WxLoginVo register(ChPatientAccountBo bo);

    ChPatientAccountVo queryByPhone(String phone);

    ChPatientAccountVo queryByOpenid(String openid);

    ChPatientAccountVo queryByPatientId(Long patientId);

    List<ChPatientAccountVo> queryFamilyProxies(Long masterAccountId);

    Boolean bindFamilyProxy(ChPatientAccountBo bo);

    Boolean unbindFamilyProxy(Long accountId);

    Boolean updateAuthScope(Long accountId, String authScope);

    WxLoginVo loginByWxCode(WxLoginCodeBo bo);

    /**
     * 已登录用户绑定微信（通过 wx.login code 获取 openid）
     */
    Boolean bindWechat(Long accountId, String code);

    /**
     * 根据账号ID查询
     */
    ChPatientAccountVo getAccountById(Long accountId);

    /**
     * 更新用户昵称和头像
     */
    Boolean updateAccountInfo(Long accountId, String nickname, String avatarOssId);
}
