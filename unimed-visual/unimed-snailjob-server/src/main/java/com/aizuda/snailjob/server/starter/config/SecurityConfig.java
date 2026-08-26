package com.aizuda.snailjob.server.starter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 权限安全配置
 *
 * @author Lion Li
 */
@Configuration
public class SecurityConfig {

    @Value("${spring.cloud.nacos.discovery.metadata.username:unimed}")
    private String username;
    @Value("${spring.cloud.nacos.discovery.metadata.userpassword:123456}")
    private String password;

    @Bean
    public FilterRegistrationBean<ActuatorAuthFilter> actuatorFilterRegistrationBean() {
        FilterRegistrationBean<ActuatorAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ActuatorAuthFilter(username, password));
        registrationBean.addUrlPatterns("/actuator", "/actuator/*");
        return registrationBean;
    }

}
