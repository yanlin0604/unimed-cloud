package org.dromara.dhcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 数字人业务服�? *
 * @author unimed
 */
@SpringBootApplication
public class UnimedDhApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(UnimedDhApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  数字人业务服务模块启动成�?  �?´ڡ`�?�?);
    }
}
