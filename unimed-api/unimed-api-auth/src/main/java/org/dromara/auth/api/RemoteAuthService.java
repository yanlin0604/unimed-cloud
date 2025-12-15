package org.dromara.auth.api;

import org.dromara.auth.api.domain.dto.RemoteApiTokenDto;
import org.dromara.auth.api.domain.dto.RemoteApiTokenRequest;
import org.dromara.auth.api.domain.vo.RemoteApiTokenValidationVo;



/**
 * 远程认证服务接口
 *
 * <p>提供给其他微服务调用的认证相关接口</p>
 * <p>支持API Key换取Token、Token验证等功能</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface RemoteAuthService {

    /**
     * 根据API Key获取Token
     *
     * <p>外部系统使用有效的API Key换取访问令牌</p>
     *
     * @param request API Token请求
     * @return Token信息，如果API Key无效则返回null
     */
    RemoteApiTokenDto generateToken(RemoteApiTokenRequest request);

    /**
     * 验证Token有效性
     *
     * <p>检查Token的有效性并返回相关信息</p>
     *
     * @param token Token值（可以包含Bearer前缀）
     * @return Token验证结果，如果Token无效则返回null
     */
    RemoteApiTokenValidationVo validateToken(String token);

    /**
     * 撤销Token
     *
     * <p>使指定的Token立即失效</p>
     *
     * @param token Token值（可以包含Bearer前缀）
     * @return 是否撤销成功
     */
    boolean revokeToken(String token);

    /**
     * 检查限流
     *
     * <p>检查指定API Key是否超过限流阈值</p>
     *
     * @param keyName API Key名称
     * @param rateLimit 每分钟最大请求数
     * @return true表示允许请求，false表示超过限流阈值
     */
    boolean checkRateLimit(String keyName, Integer rateLimit);
}