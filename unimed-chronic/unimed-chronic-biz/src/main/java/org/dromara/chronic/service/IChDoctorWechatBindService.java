package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDoctorWechatBindBo;
import org.dromara.chronic.domain.vo.ChDoctorWechatBindVo;

/**
 * 医生微信绑定服务
 *
 * @author unimed
 */
public interface IChDoctorWechatBindService {

    Long bind(ChDoctorWechatBindBo bo);

    ChDoctorWechatBindVo queryByOpenid(String openid);

    ChDoctorWechatBindVo queryByUserId(Long userId);

    Boolean unbind(Long id);
}
