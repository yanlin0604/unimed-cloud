package org.dromara.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * API Key 配置属性
 *
 * <p>用于配置 API Token 认证服务的参数，支持 Nacos 动态刷新</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "api-token.auth")
public class ApiKeyProperties {

    /**
     * 是否启用 API Token 认证
     */
    private boolean enabled = false;

    /**
     * API Key 请求头名称（用于换取 Token）
     */
    private String headerName = "X-API-Key";

    /**
     * Token 请求头名称（用于业务接口鉴权）
     */
    private String tokenHeaderName = "Authorization";

    /**
     * Token 过期时间（默认 2 小时）
     */
    private Duration tokenExpiration = Duration.ofHours(2);

    /**
     * API Key 配置列表
     */
    private List<ApiKeyConfig> keys = new ArrayList<>();

    /**
     * 单个 API Key 配置
     */
    @Data
    public static class ApiKeyConfig {
        /**
         * API Key 值
         */
        private String key;

        /**
         * 名称标识（用于日志和监控）
         */
        private String name;

        /**
         * 描述
         */
        private String description;

        /**
         * 允许访问的端点列表（支持通配符）
         */
        private List<String> allowedEndpoints = new ArrayList<>();

        /**
         * 每分钟最大请求数（0 表示不限制）
         */
        private Integer rateLimit = 0;
    }
}
