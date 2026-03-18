package org.dromara.dhcore.service;

import org.dromara.dhcore.domain.bo.portal.PasswordLoginBo;
import org.dromara.dhcore.domain.bo.portal.PortalRegisterBo;
import org.dromara.dhcore.domain.bo.portal.SmsCodeBo;
import org.dromara.dhcore.domain.bo.portal.SmsLoginBo;
import org.dromara.dhcore.domain.vo.portal.PortalLoginVo;

/**
 * C端门户认证服务接口
 *
 * @author unimed
 */
public interface IPortalAuthService {

    /**
     * 发送短信验证码
     *
     * @param bo 短信验证码请求
     * @return 开发环境返回测试验证码，生产环境返回 null
     */
    String sendSmsCode(SmsCodeBo bo);

    /**
     * 短信验证码登录
     *
     * @param bo 短信登录请求
     * @return 登录响应
     */
    PortalLoginVo smsLogin(SmsLoginBo bo);

    /**
     * 密码登录
     *
     * @param bo 密码登录请求
     * @return 登录响应
     */
    PortalLoginVo passwordLogin(PasswordLoginBo bo);

    /**
     * 用户注册
     *
     * @param bo 注册请求
     * @return 登录响应
     */
    PortalLoginVo register(PortalRegisterBo bo);

    /**
     * 登出
     */
    void logout();
}
