package org.dromara.auth.controller;

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
import org.dromara.auth.properties.ApiKeyProperties;
import org.dromara.auth.service.ApiTokenService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公共 API Token 控制器
 *
 * <p>提供不受 Sa-Token 限制的 API Token 获取接口</p>
 * <p>使用独立的路径 /public-api-token，完全绕过 Sa-Token 检查</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Tag(name = "公共 API Token 管理", description = "不受 Sa-Token 限制的 API Token 接口")
@Slf4j
@Validated
@RestController
@RequestMapping("/public-api-token")
@RequiredArgsConstructor
public class PublicApiTokenController {

    private final ApiTokenService apiTokenService;
    private final ApiKeyProperties apiKeyProperties;

    /**
     * 诊断接口 - 检查配置状态
     */
    @Operation(summary = "配置诊断", description = "检查 API Token 服务配置状态")
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
     * 验证 API Token
     *
     * <p>检查 Token 的有效性并返回相关信息</p>
     *
     * @param token Token 值
     * @return Token 验证结果
     */
    @Operation(summary = "验证 API Token", description = "检查 Token 的有效性并返回相关信息")
    @GetMapping("/validate")
    public R<Object> validateToken(
        @Parameter(description = "Token 值", required = true)
        @RequestParam String token
    ) {
        try {
            if (token == null || token.isBlank()) {
                return R.fail("Token 参数不能为空");
            }

            log.debug("收到 Token 验证请求");

            var keyConfigOpt = apiTokenService.validateToken(token);
            if (keyConfigOpt.isEmpty()) {
                return R.ok("Token 验证完成", java.util.Map.of("valid", false));
            }

            var keyConfig = keyConfigOpt.get();
            
            var validationResult = java.util.Map.of(
                "valid", true,
                "keyName", keyConfig.getName(),
                "allowedEndpoints", keyConfig.getAllowedEndpoints()
            );

            return R.ok("Token 验证完成", validationResult);
        } catch (Exception e) {
            log.error("Token 验证过程中发生异常", e);
            return R.fail("Token 验证失败: " + e.getMessage());
        }
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