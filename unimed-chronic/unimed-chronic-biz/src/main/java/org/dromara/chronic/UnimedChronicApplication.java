package org.dromara.chronic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 慢病业务服务
 *
 * @author unimed
 */
@SpringBootApplication
public class UnimedChronicApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(UnimedChronicApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(=^･ω･^=) 慢病业务服务模块启动成功");
    }
}
