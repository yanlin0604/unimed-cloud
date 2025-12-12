package org.dromara.auth.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.auth.domain.dto.ApiTokenDto;
import org.dromara.auth.domain.dto.ApiTokenRequest;
import org.dromara.auth.domain.vo.ApiTokenValidationVo;
import org.dromara.auth.properties.ApiKeyProperties;
import org.dromara.auth.service.ApiTokenService;
import org.dromara.common.core.domain.R;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * API Token 控制器
 *
 * <p>提供 API Token 的获取、撤销和验证接口</p>
 * <p>支持外部系统通过 API Key 获取访问令牌</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Tag(name = "API Token 管理", description = "API Token 获取、撤销和验证接口")
@Slf4j
@Validated
@RestController
@RequestMapping("/api-token")
@RequiredArgsConstructor
public class ApiTokenController {

    private final ApiTokenService apiTokenService;
    private final ApiKeyProperties apiKeyProperties;

    /**
     * 诊断接口 - 检查配置状态
     */
    @GetMapping("/debug")
    public R<Object> debug() {
        try {
            var debugInfo = new java.util.HashMap<String, Object>();
            debugInfo.put("enabled", apiKeyProperties.isEnabled());
            debugInfo.put("keysCount", apiKeyProperties.getKeys() != null ? apiKeyProperties.getKeys().size() : 0);
            debugInfo.put("tokenExpiration", apiKeyProperties.getTokenExpiration().toString());
            
            if (apiKeyProperties.getKeys() != null) {
                var keyNames = apiKeyProperties.getKeys().stream()
                    .map(ApiKeyProperties.ApiKeyConfig::getName)
                    .collect(java.util.stream.Collectors.toList());
                debugInfo.put("keyNames", keyNames);
            }
            
            return R.ok("配置状态", debugInfo);
        } catch (Exception e) {
            log.error("获取配置状态失败", e);
            return R.fail("配置获取失败: " + e.getMessage());
        }
    }

    /**
     * 获取 API Token
     *
     * <p>外部系统使用有效的 API Key 换取访问令牌</p>
     *
     * @param request API Token 请求
     * @return Token 信息
     */
    @Operation(summary = "获取 API Token", description = "使用 API Key 获取访问令牌")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token 获取成功"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "401", description = "API Key 无效"),
        @ApiResponse(responseCode = "429", description = "请求过于频繁")
    })
    @PostMapping("/token")
    public R<ApiTokenDto> getToken(
        @Parameter(description = "API Token 请求", required = true)
        @Valid @RequestBody ApiTokenRequest request
    ) {
        try {
            log.info("收到 Token 获取请求 - API Key: {}", maskApiKey(request.apiKey()));

            // 检查配置是否启用
            if (!apiKeyProperties.isEnabled()) {
                log.warn("API Token 认证未启用");
                return R.fail("API Token 认证服务未启用");
            }

            // 检查限流
            var keyConfigOpt = findApiKeyConfig(request.apiKey());
            if (keyConfigOpt.isPresent()) {
                var keyConfig = keyConfigOpt.get();
                if (!apiTokenService.checkRateLimit(keyConfig.getName(), keyConfig.getRateLimit())) {
                    log.warn("Token 获取被限流 - API Key: {}", keyConfig.getName());
                    return R.fail("请求过于频繁，请稍后重试");
                }
            }

            // 生成 Token
            var tokenOpt = apiTokenService.generateToken(request.apiKey());
            if (tokenOpt.isEmpty()) {
                log.warn("Token 生成失败 - 无效的 API Key");
                return R.fail("无效的 API Key");
            }

            return R.ok("Token 获取成功", tokenOpt.get());
        } catch (Exception e) {
            log.error("Token 获取过程中发生异常", e);
            return R.fail("Token 获取失败: " + e.getMessage());
        }
    }

    /**
     * 撤销 API Token
     *
     * <p>使指定的 Token 立即失效</p>
     *
     * @param authorization Authorization 头，格式为 "Bearer {token}"
     * @return 撤销结果
     */
    @Operation(summary = "撤销 API Token", description = "使指定的 Token 立即失效")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token 撤销成功"),
        @ApiResponse(responseCode = "400", description = "Token 格式无效"),
        @ApiResponse(responseCode = "404", description = "Token 不存在")
    })
    @PostMapping("/revoke")
    public R<Void> revokeToken(
        @Parameter(description = "Authorization 头，格式为 'Bearer {token}'", required = true)
        @RequestHeader("Authorization") String authorization
    ) {
        if (StrUtil.isBlank(authorization)) {
            return R.fail("缺少 Authorization 头");
        }

        if (!authorization.startsWith("Bearer ")) {
            return R.fail("Authorization 头格式错误，应为 'Bearer {token}'");
        }

        log.info("收到 Token 撤销请求");

        var success = apiTokenService.revokeToken(authorization);
        if (!success) {
            return R.fail("Token 不存在或已过期");
        }

        return R.ok("Token 撤销成功");
    }

    /**
     * 验证 API Token
     *
     * <p>检查 Token 的有效性并返回相关信息</p>
     *
     * @param token Token 值
     * @return Token 验证结果
     */
    @Operation(summary = "验证 API Token", description = "检查 Token 的有效性并返回相关信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "验证完成"),
        @ApiResponse(responseCode = "400", description = "Token 参数缺失")
    })
    @GetMapping("/validate")
    public R<ApiTokenValidationVo> validateToken(
        @Parameter(description = "Token 值", required = true)
        @RequestParam String token
    ) {
        if (StrUtil.isBlank(token)) {
            return R.fail("Token 参数不能为空");
        }

        log.debug("收到 Token 验证请求");

        var keyConfigOpt = apiTokenService.validateToken(token);
        if (keyConfigOpt.isEmpty()) {
            return R.ok("Token 验证完成", ApiTokenValidationVo.invalid());
        }

        var keyConfig = keyConfigOpt.get();
        
        // 计算剩余有效时间
        var remainingSeconds = calculateRemainingSeconds(token);

        var validationResult = ApiTokenValidationVo.valid(
            keyConfig.getName(),
            remainingSeconds,
            keyConfig.getAllowedEndpoints()
        );

        return R.ok("Token 验证完成", validationResult);
    }

    /**
     * 查找 API Key 配置
     *
     * @param apiKey API Key 值
     * @return API Key 配置
     */
    private java.util.Optional<ApiKeyProperties.ApiKeyConfig> findApiKeyConfig(String apiKey) {
        if (apiKeyProperties.getKeys() == null || apiKeyProperties.getKeys().isEmpty()) {
            return java.util.Optional.empty();
        }

        return apiKeyProperties.getKeys().stream()
            .filter(k -> k.getKey() != null && k.getKey().equals(apiKey))
            .findFirst();
    }

    /**
     * 计算 Token 剩余有效时间
     *
     * @param token Token 值
     * @return 剩余秒数
     */
    private Long calculateRemainingSeconds(String token) {
        var actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        var tokenKey = "auth:api-token:" + actualToken;
        
        try {
            return RedisUtils.getTimeToLive(tokenKey);
        } catch (Exception e) {
            log.warn("获取 Token 剩余时间失败", e);
            return null;
        }
    }

    /**
     * 脱敏 API Key
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