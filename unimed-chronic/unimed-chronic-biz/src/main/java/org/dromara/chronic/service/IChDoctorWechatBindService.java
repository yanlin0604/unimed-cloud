package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDoctorWechatBindBo;
import org.dromara.chronic.domain.vo.ChDoctorWechatBindVo;
import org.dromara.chronic.domain.vo.DoctorLoginVo;

/**
 * 医生微信绑定服务
 *
 * @author unimed
 */
public interface IChDoctorWechatBindService {

    Long bind(ChDoctorWechatBindBo bo);

    /**
     * 微信小程序 code 登录：换取 openid -> 查绑定的 sys_user -> 签发 token
     *
     * @param code 小程序 wx.login 返回的 code
     * @return 登录结果；未绑定时 needBind=true 且不签发 token
     */
    DoctorLoginVo loginByWxCode(String code);

    ChDoctorWechatBindVo queryByOpenid(String openid);

    ChDoctorWechatBindVo queryByUserId(Long userId);

    Boolean unbind(Long id);
}
