package org.dromara.dh.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.dh.service.IAuthClientService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * API Key认证过滤器
 *
 * <p>对外部API接口进行API Key认证</p>
 * <p>支持通过API Key直接认证或通过Token认证</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class ApiKeyAuthFilter implements Filter {

    /**
     * API Key请求头名称
     */
    public static final String API_KEY_HEADER = "X-API-Key";

    /**
     * Token请求头名称
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * API Key名称属性键（存储在request attribute中）
     */
    public static final String API_KEY_NAME_ATTRIBUTE = "apiKeyName";

    /**
     * 需要认证的路径模式
     */
    private static final List<String> PROTECTED_PATTERNS = List.of(
        "/api/v1/dh/external/**"
    );

    /**
     * 不需要认证的路径模式
     */
    private static final List<String> EXCLUDED_PATTERNS = List.of(
        "/api/v1/dh/external/health",
        "/api/v1/dh/external/debug",
        "/actuator/**",
        "/health",
        "/info",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/webjars/**",
        "/favicon.ico",
        "/error"
    );

    private final IAuthClientService authClientService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        var requestPath = httpRequest.getRequestURI();
        var method = httpRequest.getMethod();

        log.debug("API Key认证过滤器 - 请求路径: {}, 方法: {}", requestPath, method);

        // 检查是否需要认证
        if (!needsAuthentication(requestPath)) {
            log.debug("路径不需要认证，直接放行: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        // 执行认证
        var authResult = authenticate(httpRequest);
        if (!authResult.isSuccess()) {
            log.warn("API认证失败 - 路径: {}, 原因: {}", requestPath, authResult.getMessage());
            writeErrorResponse(httpResponse, authResult);
            return;
        }

        // 设置API Key名称到request attribute
        httpRequest.setAttribute(API_KEY_NAME_ATTRIBUTE, authResult.getApiKeyName());

        log.debug("API认证成功 - 路径: {}, API Key: {}", requestPath, authResult.getApiKeyName());
        chain.doFilter(request, response);
    }

    /**
     * 检查路径是否需要认证
     *
     * @param requestPath 请求路径
     * @return 是否需要认证
     */
    private boolean needsAuthentication(String requestPath) {
        // 先检查排除列表
        for (String excludedPattern : EXCLUDED_PATTERNS) {
            if (pathMatcher.match(excludedPattern, requestPath)) {
                return false;
            }
        }

        // 再检查保护列表
        for (String protectedPattern : PROTECTED_PATTERNS) {
            if (pathMatcher.match(protectedPattern, requestPath)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 执行认证
     *
     * @param request HTTP请求
     * @return 认证结果
     */
    private AuthResult authenticate(HttpServletRequest request) {
        var apiKey = request.getHeader(API_KEY_HEADER);
        var authorization = request.getHeader(AUTHORIZATION_HEADER);

        // 优先使用API Key认证
        if (apiKey != null && !apiKey.isBlank()) {
            return authenticateWithApiKey(apiKey, request.getRequestURI());
        }

        // 其次使用Token认证
        if (authorization != null && !authorization.isBlank()) {
            return authenticateWithToken(authorization, request.getRequestURI());
        }

        return AuthResult.failure("缺少认证信息，请提供X-API-Key或Authorization头");
    }

    /**
     * 使用API Key进行认证
     *
     * @param apiKey      API Key
     * @param requestPath 请求路径
     * @return 认证结果
     */
    private AuthResult authenticateWithApiKey(String apiKey, String requestPath) {
        try {
            // 通过API Key获取Token
            var tokenOpt = authClientService.getTokenByApiKey(apiKey);
            if (tokenOpt.isEmpty()) {
                return AuthResult.failure("无效的API Key");
            }

            var token = tokenOpt.get();

            // 检查端点权限
            if (!authClientService.isEndpointAllowed(token, requestPath)) {
                return AuthResult.failure("API Key无权限访问此端点");
            }

            // 获取API Key名称
            var apiKeyName = authClientService.getApiKeyName(token);
            return AuthResult.success(apiKeyName != null ? apiKeyName : "unknown");

        } catch (Exception e) {
            log.error("API Key认证过程中发生异常", e);
            return AuthResult.failure("认证服务异常");
        }
    }

    /**
     * 使用Token进行认证
     *
     * @param authorization Authorization头
     * @param requestPath   请求路径
     * @return 认证结果
     */
    private AuthResult authenticateWithToken(String authorization, String requestPath) {
        try {
            // 验证Token格式
            if (!authorization.startsWith("Bearer ")) {
                return AuthResult.failure("Token格式错误，应为'Bearer {token}'");
            }

            // 验证Token有效性
            var validationOpt = authClientService.validateToken(authorization);
            if (validationOpt.isEmpty() || !validationOpt.get().valid()) {
                return AuthResult.failure("无效的Token");
            }

            var validation = validationOpt.get();

            // 检查端点权限
            if (!validation.isEndpointAllowed(requestPath)) {
                return AuthResult.failure("Token无权限访问此端点");
            }

            return AuthResult.success(validation.keyName());

        } catch (Exception e) {
            log.error("Token认证过程中发生异常", e);
            return AuthResult.failure("认证服务异常");
        }
    }

    /**
     * 写入错误响应
     *
     * @param response   HTTP响应
     * @param authResult 认证结果
     * @throws IOException IO异常
     */
    private void writeErrorResponse(HttpServletResponse response, AuthResult authResult) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var errorResponse = R.fail(authResult.getMessage());
        var jsonResponse = JsonUtils.toJsonString(errorResponse);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    /**
     * 认证结果
     */
    private static class AuthResult {
        private final boolean success;
        private final String message;
        private final String apiKeyName;

        private AuthResult(boolean success, String message, String apiKeyName) {
            this.success = success;
            this.message = message;
            this.apiKeyName = apiKeyName;
        }

        public static AuthResult success(String apiKeyName) {
            return new AuthResult(true, "认证成功", apiKeyName);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getApiKeyName() {
            return apiKeyName;
        }
    }
}