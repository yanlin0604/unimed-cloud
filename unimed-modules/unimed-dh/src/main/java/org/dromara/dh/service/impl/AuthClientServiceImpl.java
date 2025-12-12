package org.dromara.dh.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.api.RemoteAuthService;
import org.dromara.auth.api.domain.dto.RemoteApiTokenRequest;
import org.dromara.auth.api.domain.vo.RemoteApiTokenValidationVo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.dh.service.IAuthClientService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * 认证客户端服务实现
 *
 * <p>通过Dubbo调用认证服务，提供Token管理功能</p>
 * <p>支持本地缓存以提高性能</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthClientServiceImpl implements IAuthClientService {

    /**
     * Token缓存键前缀
     */
    private static final String TOKEN_CACHE_PREFIX = "dh:auth:token:";

    /**
     * 验证结果缓存键前缀
     */
    private static final String VALIDATION_CACHE_PREFIX = "dh:auth:validation:";

    /**
     * 缓存过期时间（5分钟）
     */
    private static final Duration CACHE_DURATION = Duration.ofMinutes(5);

    @DubboReference
    private RemoteAuthService remoteAuthService;

    /**
     * 根据API Key获取Token
     *
     * @param apiKey API Key
     * @return Token信息，如果API Key无效则返回空
     */
    @Override
    public Optional<String> getTokenByApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            log.debug("API Key为空，无法获取Token");
            return Optional.empty();
        }

        try {
            // 先检查缓存
            var cacheKey = TOKEN_CACHE_PREFIX + hashApiKey(apiKey);
            var cachedToken = RedisUtils.<String>getCacheObject(cacheKey);
            if (cachedToken != null) {
                log.debug("从缓存获取Token成功");
                return Optional.of(cachedToken);
            }

            // 调用远程服务
            var request = RemoteApiTokenRequest.of(apiKey);
            var tokenOpt = remoteAuthService.generateToken(request);

            if (tokenOpt.isPresent()) {
                var token = tokenOpt.get();
                var authHeader = token.getAuthorizationHeader();

                // 缓存Token（缓存时间比实际过期时间短一些）
                var cacheExpiration = Duration.ofSeconds(Math.min(
                    token.expiresIn() - 60, // 提前1分钟过期
                    CACHE_DURATION.getSeconds()
                ));
                RedisUtils.setCacheObject(cacheKey, authHeader, cacheExpiration);

                log.info("API Key验证成功，Token获取完成");
                return Optional.of(authHeader);
            } else {
                log.warn("API Key验证失败，无法获取Token");
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("获取Token时发生异常", e);
            return Optional.empty();
        }
    }

    /**
     * 验证Token有效性
     *
     * @param token Token值（可以包含Bearer前缀）
     * @return Token验证结果，如果Token无效则返回空
     */
    @Override
    public Optional<RemoteApiTokenValidationVo> validateToken(String token) {
        if (token == null || token.isBlank()) {
            log.debug("Token为空，验证失败");
            return Optional.of(RemoteApiTokenValidationVo.invalid());
        }

        try {
            // 先检查缓存
            var cacheKey = VALIDATION_CACHE_PREFIX + hashToken(token);
            var cachedValidation = RedisUtils.<RemoteApiTokenValidationVo>getCacheObject(cacheKey);
            if (cachedValidation != null) {
                log.debug("从缓存获取Token验证结果");
                return Optional.of(cachedValidation);
            }

            // 调用远程服务
            var validationOpt = remoteAuthService.validateToken(token);
            if (validationOpt.isPresent()) {
                var validation = validationOpt.get();

                // 缓存验证结果
                RedisUtils.setCacheObject(cacheKey, validation, CACHE_DURATION);

                log.debug("Token验证完成 - 有效: {}", validation.valid());
                return Optional.of(validation);
            } else {
                log.debug("Token验证失败 - 远程服务返回空结果");
                return Optional.of(RemoteApiTokenValidationVo.invalid());
            }

        } catch (Exception e) {
            log.error("验证Token时发生异常", e);
            return Optional.of(RemoteApiTokenValidationVo.invalid());
        }
    }

    /**
     * 验证API Key是否有效
     *
     * @param apiKey API Key
     * @return 是否有效
     */
    @Override
    public boolean isValidApiKey(String apiKey) {
        return getTokenByApiKey(apiKey).isPresent();
    }

    /**
     * 检查Token是否允许访问指定端点
     *
     * @param token    Token值
     * @param endpoint 端点路径
     * @return 是否允许访问
     */
    @Override
    public boolean isEndpointAllowed(String token, String endpoint) {
        var validationOpt = validateToken(token);
        if (validationOpt.isEmpty()) {
            return false;
        }

        var validation = validationOpt.get();
        return validation.isEndpointAllowed(endpoint);
    }

    /**
     * 获取Token关联的API Key名称
     *
     * @param token Token值
     * @return API Key名称，如果Token无效则返回null
     */
    @Override
    public String getApiKeyName(String token) {
        var validationOpt = validateToken(token);
        if (validationOpt.isPresent() && validationOpt.get().valid()) {
            return validationOpt.get().keyName();
        }
        return null;
    }

    /**
     * 对API Key进行哈希处理（用于缓存键）
     *
     * @param apiKey API Key
     * @return 哈希值
     */
    private String hashApiKey(String apiKey) {
        return String.valueOf(apiKey.hashCode());
    }

    /**
     * 对Token进行哈希处理（用于缓存键）
     *
     * @param token Token
     * @return 哈希值
     */
    private String hashToken(String token) {
        // 移除Bearer前缀后再哈希
        var actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return String.valueOf(actualToken.hashCode());
    }
}