package org.dromara.dh;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 数字人微服务模块（本地版本 - 无 Nacos）
 * @author unimed
 * @since 2.5.1
 */
@EnableDubbo
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class UnimedDhApplication {

    public static void main(String[] args) {
        var application = new SpringApplication(UnimedDhApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);

        System.out.println("(♥◠‿◠)ﾉﾞ  数字人微服务启动成功 (本地模式)   ლ(´ڡ`ლ)ﾞ  ");
        System.out.println("服务地址: http://localhost:9205");
        System.out.println("健康检查: http://localhost:9205/actuator/health");
    }
}
