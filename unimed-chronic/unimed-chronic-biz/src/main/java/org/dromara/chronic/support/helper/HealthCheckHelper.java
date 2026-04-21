package org.dromara.chronic.support.helper;

import lombok.Data;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康巡检辅助类
 *
 * @author unimed
 */
@Component
public class HealthCheckHelper {

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;

    public HealthCheckHelper(DataSource dataSource, RedisConnectionFactory redisConnectionFactory) {
        this.dataSource = dataSource;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public Map<String, ComponentStatus> checkAll() {
        Map<String, ComponentStatus> result = new LinkedHashMap<>();
        result.put("mysql", checkMysql());
        result.put("redis", checkRedis());
        return result;
    }

    private ComponentStatus checkMysql() {
        ComponentStatus status = new ComponentStatus();
        status.setName("MySQL");
        try (Connection conn = dataSource.getConnection()) {
            boolean valid = conn.isValid(5);
            status.setHealthy(valid);
            status.setDetail(valid ? "连接正常" : "连接验证失败");
        } catch (Exception e) {
            status.setHealthy(false);
            status.setDetail("连接失败: " + e.getMessage());
        }
        return status;
    }

    private ComponentStatus checkRedis() {
        ComponentStatus status = new ComponentStatus();
        status.setName("Redis");
        try {
            String pong = redisConnectionFactory.getConnection().ping();
            status.setHealthy("PONG".equalsIgnoreCase(pong));
            status.setDetail(status.isHealthy() ? "连接正常" : "PING返回异常: " + pong);
        } catch (Exception e) {
            status.setHealthy(false);
            status.setDetail("连接失败: " + e.getMessage());
        }
        return status;
    }

    @Data
    public static class ComponentStatus {
        private String name;
        private boolean healthy;
        private String detail;
    }
}
