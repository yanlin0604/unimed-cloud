package org.dromara.dh.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.HttpStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数字人模块安全配置
 *
 * <p>专门为数字人模块配置的安全策略</p>
 * <p>外部接口使用自定义的API Key认证，不走Sa-Token</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Configuration
public class DhSecurityConfiguration {

    /**
     * 数字人模块专用的Sa-Token过滤器
     * 
     * <p>排除外部接口，这些接口使用ApiKeyAuthFilter进行认证</p>
     */
    @Bean
    @ConditionalOnProperty(name = "spring.application.name", havingValue = "unimed-dh")
    public SaServletFilter dhSaServletFilter() {
        return new SaServletFilter()
            .addInclude("/**")
            .addExclude(
                // 外部接口使用自定义认证
                "/api/v1/dh/external/**",
                // 健康检查接口
                "/actuator/**",
                "/health", "/info",
                // 接口文档
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/webjars/**",
                // 静态资源
                "/favicon.ico",
                "/error"
            )
            .setAuth(obj -> {
                // 对于需要Sa-Token认证的接口，这里可以添加自定义认证逻辑
                log.debug("Sa-Token认证检查 - 请求路径: {}", obj);
            })
            .setError(e -> {
                log.warn("Sa-Token认证失败: {}", e.getMessage());
                return SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED);
            });
    }
}