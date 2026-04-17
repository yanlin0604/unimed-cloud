package org.dromara.dh.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.filter.ApiKeyAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 数字人模块Web配置
 *
 * <p>配置过滤器、CORS等Web相关设置</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DhWebConfiguration {

    private final ApiKeyAuthFilter apiKeyAuthFilter;

    /**
     * 注册API Key认证过滤器
     */
    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilterRegistration() {
        var registration = new FilterRegistrationBean<ApiKeyAuthFilter>();
        registration.setFilter(apiKeyAuthFilter);
        registration.addUrlPatterns("/api/v1/dh/external/*");
        registration.setName("apiKeyAuthFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        
        log.info("API Key认证过滤器已注册 - 拦截路径: /api/v1/dh/external/*");
        return registration;
    }

    /**
     * CORS配置
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/dh/external/**", corsConfig);

        var registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("dhCorsFilter");
        
        log.info("数字人模块CORS过滤器已注册");
        return registration;
    }
}