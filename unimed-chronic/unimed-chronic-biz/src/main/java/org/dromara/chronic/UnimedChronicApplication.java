package org.dromara.chronic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 慢病业务服务
 * <p>
 * {@link EnableAsync} 已由 {@code unimed-common-core} 的 ApplicationConfig 全局开启，
 * 此处显式声明用于保障本模块独立启动时（如测试/精简上下文）异步能力也不会缺失。
 *
 * @author unimed
 */
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
public class UnimedChronicApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(UnimedChronicApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(=^･ω･^=) 慢病业务服务模块启动成功");
    }
}
