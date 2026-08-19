package org.dromara.chronic.support;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.api.RemoteAuthService;
import org.dromara.auth.api.domain.vo.RemoteApiTokenValidationVo;
import org.dromara.chronic.config.ChronicOpenapiAuthProperties;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 慢病 openapi 层 API Token 校验拦截器
 * <p>
 * 为什么需要它：{@code controller/openapi/} 的 8 个控制器原先没有任何鉴权注解，
 * 而网关只对非白名单路径做 {@code StpUtil.checkLogin()}。结果是<b>任意登录身份都能调
 * 用全部 openapi</b>——患者 token 也可以，能给任意 patientId 注入伪造指标并触发预警、
 * 经 HIS/LIS/PACS 注入伪造临床数据、任意绑定解绑他人设备。
 * <p>
 * 这里复用系统既有的 API Token 设施（{@link RemoteAuthService}，实现在 unimed-auth，
 * 数字人外部接口 {@code ApiKeyAuthFilter} 是同一套机制的先例），在「登录态」之外再加一层
 * 「必须持有 API Token」的校验，把 openapi 与普通业务身份隔离开。
 * <p>
 * 三档模式见 {@link ChronicOpenapiAuthProperties#getAuthMode()}。默认 observe 只告警不拦截，
 * 便于先从日志确认没有真实调用方会被 enforce 打断；确认后把配置改成 enforce 即收紧。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChronicOpenapiAuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    /** 429 Too Many Requests；项目的 HttpStatus 常量表未收录该状态码，故此处直接定义 */
    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private final ChronicOpenapiAuthProperties properties;

    @DubboReference
    private RemoteAuthService remoteAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (properties.isOff()) {
            return true;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String token = resolveToken(request);

        // 没带凭证：observe 记录、enforce 拒绝
        if (StringUtils.isBlank(token)) {
            return handleReject(uri, method, false, "未携带 API Token");
        }

        RemoteApiTokenValidationVo validation;
        try {
            validation = remoteAuthService.validateToken(token);
        } catch (Exception e) {
            // Dubbo 不可用时不能默认放行：openapi 是写入类对外接口，放行等于回到无鉴权状态。
            // observe 模式下仍只告警，由 handleReject 统一决定是否拦截。
            log.error("openapi-auth: 调用 auth 服务校验 Token 异常, uri={}", uri, e);
            return handleReject(uri, method, true, "凭证校验服务不可用: " + e.getMessage());
        }

        // 接口约定 Token 无效时可能返回 null，也可能返回 invalid()，两种都要兜住
        if (validation == null || !validation.valid()) {
            return handleReject(uri, method, true, "API Token 无效或已过期");
        }

        // 端点白名单：一把钥匙只开一扇门
        if (Boolean.TRUE.equals(properties.getCheckAllowedEndpoints())
            && !validation.isEndpointAllowed(uri)) {
            return handleReject(uri, method, true,
                "API Token[" + validation.keyName() + "] 无权访问该端点");
        }

        // 限流：阈值由慢病侧配置（auth 的校验结果里不带该 Key 的限流阈值，只带 keyName）
        Integer rateLimit = properties.getRateLimit();
        if (rateLimit != null && rateLimit > 0) {
            try {
                if (!remoteAuthService.checkRateLimit(validation.keyName(), rateLimit)) {
                    log.warn("openapi-auth: 限流触发, keyName={}, limit={}/min, uri={}",
                        validation.keyName(), rateLimit, uri);
                    throw new ServiceException("请求过于频繁，请稍后重试", HTTP_TOO_MANY_REQUESTS);
                }
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                // 限流是加固手段，其自身故障不应阻断业务
                log.warn("openapi-auth: 限流检查异常，放行, keyName={}, err={}",
                    validation.keyName(), e.getMessage());
            }
        }

        log.debug("openapi-auth: 校验通过, keyName={}, {} {}", validation.keyName(), method, uri);
        return true;
    }

    /**
     * 从请求头提取 API Token
     * <p>
     * 优先独立请求头（默认 {@code X-Chronic-Api-Token}）。之所以不直接用
     * {@code Authorization}：Sa-Token 的 token-name 就是它，而 openapi 未加入网关白名单，
     * 网关要用该头做登录校验，两者会互相抢占。
     */
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(properties.getTokenHeaderName());
        if (StringUtils.isNotBlank(token)) {
            return stripBearer(token);
        }
        if (Boolean.TRUE.equals(properties.getAllowAuthorizationHeader())) {
            String authorization = request.getHeader("Authorization");
            // 仅当带 Bearer 前缀时才当作 API Token，避免把 Sa-Token 会话票据误当凭证送去校验
            if (StringUtils.isNotBlank(authorization) && authorization.startsWith(BEARER_PREFIX)) {
                return stripBearer(authorization);
            }
        }
        return null;
    }

    private String stripBearer(String token) {
        return token.startsWith(BEARER_PREFIX) ? token.substring(BEARER_PREFIX.length()).trim() : token.trim();
    }

    /**
     * 统一处置校验失败
     *
     * @param tokenPresent 是否带了凭证（便于运维区分「没带」与「带了但无效」）
     * @return observe 模式返回 true 放行；enforce 模式抛异常
     */
    private boolean handleReject(String uri, String method, boolean tokenPresent, String reason) {
        // 打印调用方身份，供运维判断是否存在真实外部调用方
        log.warn("openapi-auth[{}]: {} —— {} {}, tokenPresent={}, userId={}, userType={}, clientId={}",
            properties.getAuthMode(), reason, method, uri, tokenPresent,
            safeUserId(), safeUserType(), safeClientId());

        if (properties.isEnforce()) {
            throw new ServiceException("openapi 接口需提供有效的 API Token", HttpStatus.UNAUTHORIZED);
        }
        return true;
    }

    private Object safeUserId() {
        try {
            return LoginHelper.getUserId();
        } catch (Exception e) {
            return null;
        }
    }

    private Object safeUserType() {
        try {
            return LoginHelper.getUserType();
        } catch (Exception e) {
            return null;
        }
    }

    private Object safeClientId() {
        try {
            return StpUtil.getExtra(LoginHelper.CLIENT_KEY);
        } catch (Exception e) {
            return null;
        }
    }
}
