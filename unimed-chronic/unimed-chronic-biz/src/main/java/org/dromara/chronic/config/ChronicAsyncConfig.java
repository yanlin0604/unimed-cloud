package org.dromara.chronic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 慢病业务异步执行器配置
 * <p>
 * `@EnableAsync` 已由 {@code unimed-common-core} 的 {@code ApplicationConfig} 全局开启，
 * 本类仅用于注册慢病域专属线程池 {@code chronicAsyncExecutor}。
 * <p>
 * 典型使用场景：HIS 同步接口中，确诊后触发的风险评估、方案草案生成等耗时任务。
 *
 * @author unimed
 */
@Configuration
public class ChronicAsyncConfig {

    /**
     * 慢病专属异步执行器
     * <p>
     * 可通过 Nacos 配置 `chronic.async.*` 覆盖默认值，修改无需重启即可灰度调优。
     * 拒绝策略采用 `CallerRunsPolicy`：队列满时由调用线程执行，保证任务不丢失。
     */
    @Bean("chronicAsyncExecutor")
    public TaskExecutor chronicAsyncExecutor(
            @Value("${chronic.async.core-size:4}") int coreSize,
            @Value("${chronic.async.max-size:16}") int maxSize,
            @Value("${chronic.async.queue-capacity:100}") int queueCapacity,
            @Value("${chronic.async.keep-alive-seconds:60}") int keepAliveSeconds) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("chronic-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
