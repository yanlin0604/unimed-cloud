package org.dromara.dh.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.api.RemoteAuthService;
import org.dromara.auth.api.domain.dto.RemoteApiTokenDto;
import org.dromara.auth.api.domain.dto.RemoteApiTokenRequest;
import org.dromara.auth.api.domain.vo.RemoteApiTokenValidationVo;
import org.dromara.dh.domain.vo.TokenValidationVo;
import org.dromara.dh.service.IAuthClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 认证客户端服务实现
 *
 * <p>通过Dubbo调用认证服务，提供Token管理功能</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthClientServiceImpl implements IAuthClientService {

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
            var request = RemoteApiTokenRequest.of(apiKey);
            var tokenResult = remoteAuthService.generateToken(request);
            var tokenDto = unwrapTokenDto(tokenResult);

            if (tokenDto != null) {
                var authHeader = tokenDto.getAuthorizationHeader();
                if (authHeader != null && !authHeader.isBlank()) {
                    log.info("API Key验证成功，Token获取完成");
                    return Optional.of(authHeader);
                }
            }
            
            log.warn("API Key验证失败，无法获取Token");
            return Optional.empty();

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
    public Optional<TokenValidationVo> validateToken(String token) {
        if (token == null || token.isBlank()) {
            log.debug("Token为空，验证失败");
            return Optional.of(TokenValidationVo.invalid());
        }

        try {
            var validationResult = remoteAuthService.validateToken(token);
            var remoteValidation = unwrapTokenValidation(validationResult);
            
            if (remoteValidation != null) {
                // 转换远程验证结果为本地类型
                var localValidation = new TokenValidationVo(
                    remoteValidation.valid(),
                    remoteValidation.keyName(),
                    remoteValidation.remainingSeconds(),
                    remoteValidation.allowedEndpoints()
                );

                log.debug("Token验证完成 - 有效: {}", localValidation.valid());
                return Optional.of(localValidation);
            } else {
                log.debug("Token验证失败 - 远程服务返回空结果");
                return Optional.of(TokenValidationVo.invalid());
            }

        } catch (Exception e) {
            log.error("验证Token时发生异常", e);
            return Optional.of(TokenValidationVo.invalid());
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
     * 脱敏API Key
     *
     * @param apiKey API Key
     * @return 脱敏后的API Key
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }

    private RemoteApiTokenDto unwrapTokenDto(Object tokenResult) {
        if (tokenResult == null) {
            return null;
        }

        if (tokenResult instanceof Optional<?> tokenOpt) {
            var value = tokenOpt.orElse(null);
            return value instanceof RemoteApiTokenDto tokenDto ? tokenDto : null;
        }

        return tokenResult instanceof RemoteApiTokenDto tokenDto ? tokenDto : null;
    }

    private RemoteApiTokenValidationVo unwrapTokenValidation(Object validationResult) {
        if (validationResult == null) {
            return null;
        }

        if (validationResult instanceof Optional<?> validationOpt) {
            var value = validationOpt.orElse(null);
            return value instanceof RemoteApiTokenValidationVo validationVo ? validationVo : null;
        }

        return validationResult instanceof RemoteApiTokenValidationVo validationVo ? validationVo : null;
    }
}
