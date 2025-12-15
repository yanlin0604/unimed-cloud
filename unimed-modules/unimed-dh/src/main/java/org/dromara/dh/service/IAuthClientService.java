package org.dromara.dh.service;

import org.dromara.dh.domain.vo.TokenValidationVo;

import java.util.Optional;

/**
 * 认证客户端服务接口
 *
 * <p>提供与认证服务交互的功能</p>
 * <p>支持API Key验证、Token获取等操作</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface IAuthClientService {

    /**
     * 根据API Key获取Token
     *
     * <p>调用认证服务，使用API Key换取访问令牌</p>
     *
     * @param apiKey API Key
     * @return Token信息，如果API Key无效则返回空
     */
    Optional<String> getTokenByApiKey(String apiKey);

    /**
     * 验证Token有效性
     *
     * <p>调用认证服务验证Token的有效性</p>
     *
     * @param token Token值（可以包含Bearer前缀）
     * @return Token验证结果，如果Token无效则返回空
     */
    Optional<TokenValidationVo> validateToken(String token);

    /**
     * 验证API Key是否有效
     *
     * <p>通过尝试获取Token来验证API Key的有效性</p>
     *
     * @param apiKey API Key
     * @return 是否有效
     */
    boolean isValidApiKey(String apiKey);

    /**
     * 检查Token是否允许访问指定端点
     *
     * <p>验证Token并检查是否有权限访问指定的API端点</p>
     *
     * @param token    Token值
     * @param endpoint 端点路径
     * @return 是否允许访问
     */
    boolean isEndpointAllowed(String token, String endpoint);

    /**
     * 获取Token关联的API Key名称
     *
     * <p>通过Token获取对应的API Key名称，用于日志记录</p>
     *
     * @param token Token值
     * @return API Key名称，如果Token无效则返回null
     */
    String getApiKeyName(String token);
}