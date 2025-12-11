package org.dromara.dh.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 配置类
 * 
 * <p>配置用于调用 Python 数字人 API 的 WebClient，包括超时设置、连接池配置等。</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Configuration
@EnableConfigurationProperties(WebClientConfig.DigitalHumanProperties.class)
public class WebClientConfig {

    /**
     * 数字人服务配置属性
     */
    @ConfigurationProperties(prefix = "digital-human.python-api")
    public record DigitalHumanProperties(
        String baseUrl,
        Duration timeout,
        Duration connectTimeout,
        Duration readTimeout,
        RetryConfig retry
    ) {
        public record RetryConfig(
            int maxAttempts,
            Duration delay,
            double multiplier
        ) {}
        
        public DigitalHumanProperties() {
            this("http://localhost:8080", 
                 Duration.ofSeconds(30), 
                 Duration.ofSeconds(10), 
                 Duration.ofSeconds(30),
                 new RetryConfig(3, Duration.ofSeconds(1), 2.0));
        }
    }

    /**
     * 配置用于调用 Python API 的 WebClient
     * 
     * @param properties 数字人服务配置属性
     * @return 配置好的 WebClient 实例
     */
    @Bean("digitalHumanWebClient")
    public WebClient digitalHumanWebClient(DigitalHumanProperties properties) {
        // 配置 HTTP 客户端
        var httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 
                (int) properties.connectTimeout().toMillis())
            .responseTimeout(properties.timeout())
            .doOnConnected(conn -> 
                conn.addHandlerLast(new ReadTimeoutHandler(
                        properties.readTimeout().toSeconds(), TimeUnit.SECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(
                        properties.readTimeout().toSeconds(), TimeUnit.SECONDS)));

        return WebClient.builder()
            .baseUrl(properties.baseUrl())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .codecs(configurer -> {
                // 设置内存缓冲区大小为 2MB
                configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
            })
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "application/json")
            .defaultHeader("User-Agent", "Unimed-DH-Service/2.5.1")
            .build();
    }
}