package org.dromara.auth.dubbo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.auth.api.RemoteAuthService;
import org.dromara.auth.api.domain.dto.RemoteApiTokenDto;
import org.dromara.auth.api.domain.dto.RemoteApiTokenRequest;
import org.dromara.auth.api.domain.vo.RemoteApiTokenValidationVo;
import org.dromara.auth.service.ApiTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 远程认证服务实现
 *
 * <p>提供给其他微服务调用的认证相关接口实现</p>
 * <p>基于Dubbo协议进行服务间通信</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class RemoteAuthServiceImpl implements RemoteAuthService {

    private final ApiTokenService apiTokenService;

    /**
     * 根据API Key获取Token
     *
     * @param request API Token请求
     * @return Token信息，如果API Key无效则返回空
     */
    @Override
    public Optional<RemoteApiTokenDto> generateToken(RemoteApiTokenRequest request) {
        try {
            log.debug("收到远程Token生成请求 - API Key: {}", maskApiKey(request.apiKey()));

            var tokenOpt = apiTokenService.generateToken(request.apiKey());
            if (tokenOpt.isEmpty()) {
                log.warn("远程Token生成失败 - 无效的API Key");
                return Optional.empty();
            }

            var token = tokenOpt.get();
            var remoteToken = RemoteApiTokenDto.bearer(
                token.accessToken(),
                token.expiresIn(),
                token.scope()
            );

            log.debug("远程Token生成成功");
            return Optional.of(remoteToken);

        } catch (Exception e) {
            log.error("远程Token生成过程中发生异常", e);
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
        try {
            log.debug("收到远程Token验证请求");

            var keyConfigOpt = apiTokenService.validateToken(token);
            if (keyConfigOpt.isEmpty()) {
                log.debug("远程Token验证失败 - Token无效或已过期");
                return Optional.of(RemoteApiTokenValidationVo.invalid());
            }

            var keyConfig = keyConfigOpt.get();
            var validationResult = RemoteApiTokenValidationVo.valid(
                keyConfig.getName(),
                null, // 剩余时间可以通过其他方式获取
                keyConfig.getAllowedEndpoints()
            );

            log.debug("远程Token验证成功 - API Key: {}", keyConfig.getName());
            return Optional.of(validationResult);

        } catch (Exception e) {
            log.error("远程Token验证过程中发生异常", e);
            return Optional.of(RemoteApiTokenValidationVo.invalid());
        }
    }

    /**
     * 撤销Token
     *
     * @param token Token值（可以包含Bearer前缀）
     * @return 是否撤销成功
     */
    @Override
    public boolean revokeToken(String token) {
        try {
            log.debug("收到远程Token撤销请求");

            boolean success = apiTokenService.revokeToken(token);
            if (success) {
                log.debug("远程Token撤销成功");
            } else {
                log.debug("远程Token撤销失败 - Token不存在或已过期");
            }

            return success;

        } catch (Exception e) {
            log.error("远程Token撤销过程中发生异常", e);
            return false;
        }
    }

    /**
     * 检查限流
     *
     * @param keyName   API Key名称
     * @param rateLimit 每分钟最大请求数
     * @return true表示允许请求，false表示超过限流阈值
     */
    @Override
    public boolean checkRateLimit(String keyName, Integer rateLimit) {
        try {
            log.debug("收到远程限流检查请求 - API Key: {}, 限制: {}", keyName, rateLimit);

            boolean allowed = apiTokenService.checkRateLimit(keyName, rateLimit);
            if (!allowed) {
                log.warn("远程限流检查失败 - API Key: {} 超过限制", keyName);
            }

            return allowed;

        } catch (Exception e) {
            log.error("远程限流检查过程中发生异常", e);
            // 异常情况下允许请求，避免影响业务
            return true;
        }
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
}