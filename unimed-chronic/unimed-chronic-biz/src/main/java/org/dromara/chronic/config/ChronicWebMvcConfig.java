package org.dromara.chronic.config;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.support.ChronicOpenapiAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 慢病 Web MVC 配置
 * <p>
 * 目前只做一件事：把 {@link ChronicOpenapiAuthInterceptor} 挂到 openapi 路径上。
 * <p>
 * 采用「按路径注册拦截器」而不是「逐个控制器加注解」，是因为 openapi 层将来还会
 * 继续新增对接控制器（HIS/LIS/PACS/公卫/设备…），注解方式一旦漏加就又出现一个
 * 无鉴权的对外写入端点；路径匹配能自动覆盖 {@code /chronic/openapi/**} 下的全部端点。
 *
 * @author unimed
 */
@Configuration
@RequiredArgsConstructor
public class ChronicWebMvcConfig implements WebMvcConfigurer {

    private final ChronicOpenapiAuthProperties openapiAuthProperties;
    private final ChronicOpenapiAuthInterceptor openapiAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patterns = openapiAuthProperties.getPathPatterns();
        if (patterns == null || patterns.isEmpty()) {
            return;
        }
        InterceptorRegistration registration = registry.addInterceptor(openapiAuthInterceptor)
            .addPathPatterns(patterns)
            .order(0);

        List<String> excludes = openapiAuthProperties.getExcludePathPatterns();
        if (excludes != null && !excludes.isEmpty()) {
            registration.excludePathPatterns(excludes);
        }
    }
}
