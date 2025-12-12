package org.dromara.auth.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.auth.domain.dto.ApiTokenDto;
import org.dromara.auth.properties.ApiKeyProperties;
import org.dromara.auth.properties.ApiKeyProperties.ApiKeyConfig;
import org.dromara.common.redis.utils.RedisUtils;
import org.redisson.api.RateType;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * API Token 服务
 *
 * <p>提供 API Token 的生成、验证、撤销和限流功能</p>
 * <p>Token 信息存储在 Redis 中，支持分布式部署</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiTokenService {

    /**
     * Token 缓存键前缀 - 存储 Token 对应的 API Key 名称
     */
    private static final String TOKEN_CACHE_PREFIX = "auth:api-token:";

    /**
     * Token 信息缓存键前缀 - 存储 Token 对应的完整 API Key 配置
     */
    private static final String TOKEN_INFO_PREFIX = "auth:api-token-info:";

    /**
     * 限流键前缀 - 用于记录 API Key 的请求次数
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "auth:api-key:rate:";

    /**
     * 限流时间窗口（秒）
     */
    private static final int RATE_LIMIT_INTERVAL = 60;

    private final ApiKeyProperties properties;

    /**
     * 根据 API Key 生成 Token
     *
     * <p>验证 API Key 有效性后生成 Token，并将 Token 信息存储到 Redis</p>
     *
     * @param apiKey API Key
     * @return Token 信息，如果 API Key 无效则返回空
     */
    public Optional<ApiTokenDto> generateToken(String apiKey) {
        if (StrUtil.isBlank(apiKey)) {
            log.debug("生成 Token 失败 - API Key 为空");
            return Optional.empty();
        }

        // 验证 API Key
        var keyConfigOpt = findApiKeyConfig(apiKey);
        if (keyConfigOpt.isEmpty()) {
            log.warn("生成 Token 失败 - 无效的 API Key: {}", maskApiKey(apiKey));
            return Optional.empty();
        }

        var keyConfig = keyConfigOpt.get();

        // 生成 Token
        var token = generateSecureToken(keyConfig.getName());
        var expiresIn = properties.getTokenExpiration();

        // 存储 Token 信息到 Redis
        var tokenKey = TOKEN_CACHE_PREFIX + token;
        var tokenInfoKey = TOKEN_INFO_PREFIX + token;

        RedisUtils.setCacheObject(tokenKey, keyConfig.getName(), expiresIn);
        RedisUtils.setCacheObject(tokenInfoKey, keyConfig, expiresIn);

        log.info("Token 生成成功 - API Key: {}, 有效期: {}秒", keyConfig.getName(), expiresIn.getSeconds());

        return Optional.of(ApiTokenDto.bearer(
            token,
            expiresIn.getSeconds(),
            String.join(",", keyConfig.getAllowedEndpoints())
        ));
    }

    /**
     * 验证 Token 并返回关联的 API Key 配置
     *
     * <p>从 Redis 中查询 Token 对应的 API Key 配置信息</p>
     *
     * @param token Token（可以包含 Bearer 前缀）
     * @return API Key 配置，如果 Token 无效或已过期则返回空
     */
    public Optional<ApiKeyConfig> validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            log.debug("Token 验证失败 - Token 为空");
            return Optional.empty();
        }

        // 移除 Bearer 前缀
        var actualToken = extractToken(token);

        var tokenInfoKey = TOKEN_INFO_PREFIX + actualToken;
        var keyConfig = RedisUtils.<ApiKeyConfig>getCacheObject(tokenInfoKey);

        if (keyConfig == null) {
            log.debug("Token 验证失败 - Token 不存在或已过期");
            return Optional.empty();
        }

        log.debug("Token 验证成功 - API Key: {}", keyConfig.getName());
        return Optional.of(keyConfig);
    }

    /**
     * 撤销 Token
     *
     * <p>从 Redis 中删除 Token 相关的所有缓存信息</p>
     *
     * @param token Token（可以包含 Bearer 前缀）
     * @return 是否撤销成功
     */
    public boolean revokeToken(String token) {
        if (StrUtil.isBlank(token)) {
            log.debug("Token 撤销失败 - Token 为空");
            return false;
        }

        var actualToken = extractToken(token);
        var tokenKey = TOKEN_CACHE_PREFIX + actualToken;
        var tokenInfoKey = TOKEN_INFO_PREFIX + actualToken;

        // 检查 Token 是否存在
        if (!RedisUtils.hasKey(tokenKey) && !RedisUtils.hasKey(tokenInfoKey)) {
            log.debug("Token 撤销失败 - Token 不存在");
            return false;
        }

        RedisUtils.deleteObject(tokenKey);
        RedisUtils.deleteObject(tokenInfoKey);

        log.info("Token 已撤销");
        return true;
    }

    /**
     * 检查限流
     *
     * <p>基于 Redis 的滑动窗口限流实现，检查指定 API Key 是否超过限流阈值</p>
     *
     * @param keyName   API Key 名称
     * @param rateLimit 每分钟最大请求数（0 或 null 表示不限流）
     * @return true 表示允许请求，false 表示超过限流阈值
     */
    public boolean checkRateLimit(String keyName, Integer rateLimit) {
        // 未配置限流或限流值为 0，直接放行
        if (rateLimit == null || rateLimit <= 0) {
            log.debug("限流检查 - API Key: {} 未配置限流，放行", keyName);
            return true;
        }

        var rateLimitKey = RATE_LIMIT_KEY_PREFIX + keyName;

        // 使用 Redisson 的限流器
        var result = RedisUtils.rateLimiter(rateLimitKey, RateType.OVERALL, rateLimit, RATE_LIMIT_INTERVAL);

        if (result == -1L) {
            log.warn("限流触发 - API Key: {}, 限制: {} 次/分钟", keyName, rateLimit);
            return false;
        }

        log.debug("限流检查通过 - API Key: {}, 剩余配额: {}", keyName, result);
        return true;
    }

    /**
     * 查找 API Key 配置
     *
     * @param apiKey API Key 值
     * @return API Key 配置，如果不存在则返回空
     */
    private Optional<ApiKeyConfig> findApiKeyConfig(String apiKey) {
        if (properties.getKeys() == null || properties.getKeys().isEmpty()) {
            log.debug("API Key 配置列表为空");
            return Optional.empty();
        }

        return properties.getKeys().stream()
            .filter(k -> k.getKey() != null && k.getKey().equals(apiKey))
            .findFirst();
    }

    /**
     * 生成安全的 Token
     *
     * <p>使用 UUID + 时间戳 + MD5 签名生成唯一且不可预测的 Token</p>
     *
     * @param keyName API Key 名称
     * @return 生成的 Token
     */
    private String generateSecureToken(String keyName) {
        var uuid = UUID.fastUUID().toString(true);
        var timestamp = String.valueOf(System.currentTimeMillis());
        // 使用 Sa-Token 的加密工具生成签名
        var signature = SaSecureUtil.md5(keyName + uuid + timestamp);
        return uuid + signature.substring(0, 16);
    }

    /**
     * 提取实际的 Token 值
     *
     * <p>移除 Bearer 前缀（如果存在）</p>
     *
     * @param token 原始 Token
     * @return 实际的 Token 值
     */
    private String extractToken(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    /**
     * 脱敏 API Key
     *
     * <p>保留前 4 位和后 4 位，中间用 **** 替代</p>
     *
     * @param apiKey API Key
     * @return 脱敏后的 API Key
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
