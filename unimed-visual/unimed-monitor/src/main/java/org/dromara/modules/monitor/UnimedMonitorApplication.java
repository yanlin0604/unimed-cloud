package org.dromara.modules.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 监控中心
 *
 * @author unimed
 */
@EnableAdminServer
@SpringBootApplication
public class UnimedMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnimedMonitorApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  监控中心启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
