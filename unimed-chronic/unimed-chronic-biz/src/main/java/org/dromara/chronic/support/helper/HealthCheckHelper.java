package org.dromara.chronic.support.helper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康巡检辅助类
 * <p>
 * R10: 检测 DB/Redis/Nacos/RocketMQ/HIS 五项连通性。
 * Nacos 检测通过 HTTP 调用 /nacos/v1/ns/operator/leaders；
 * RocketMQ 检测通过 HTTP 调用 broker 端点；
 * HIS 检测通过 HTTP ping 外部 HIS 接口（超时 3s）。
 *
 * @author unimed
 */
@Slf4j
@Component
public class HealthCheckHelper {

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${spring.cloud.nacos.discovery.server-addr:127.0.0.1:8848}")
    private String nacosAddr;

    @Value("${rocketmq.name-server:}")
    private String rocketmqNameServer;

    @Value("${chronic.his.health-check-url:}")
    private String hisHealthCheckUrl;

    private final RestTemplate restTemplate;

    public HealthCheckHelper(DataSource dataSource, RedisConnectionFactory redisConnectionFactory) {
        this.dataSource = dataSource;
        this.redisConnectionFactory = redisConnectionFactory;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // HIS 检测超时 3s
        factory.setReadTimeout(3000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Map<String, ComponentStatus> checkAll() {
        Map<String, ComponentStatus> result = new LinkedHashMap<>();
        result.put("mysql", checkMysql());
        result.put("redis", checkRedis());
        result.put("nacos", checkNacos());
        result.put("rocketmq", checkRocketMQ());
        result.put("his", checkHis());
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

    /**
     * R10: Nacos 健康检测
     * <p>
     * 通过 HTTP 请求 Nacos 的 /nacos/v1/ns/operator/leaders 端点验证连通性
     */
    private ComponentStatus checkNacos() {
        ComponentStatus status = new ComponentStatus();
        status.setName("Nacos");
        try {
            String url = "http://" + nacosAddr + "/nacos/v1/ns/operator/leaders";
            String response = restTemplate.getForObject(url, String.class);
            status.setHealthy(response != null);
            status.setDetail(status.isHealthy() ? "连接正常" : "返回为空");
        } catch (Exception e) {
            status.setHealthy(false);
            status.setDetail("连接失败: " + e.getMessage());
        }
        return status;
    }

    /**
     * R10: RocketMQ 健康检测
     * <p>
     * 通过 HTTP 请求 RocketMQ Broker 端点验证连通性，
     * 若未配置 name-server 则标记为 SKIP
     */
    private ComponentStatus checkRocketMQ() {
        ComponentStatus status = new ComponentStatus();
        status.setName("RocketMQ");
        if (rocketmqNameServer == null || rocketmqNameServer.isBlank()) {
            status.setHealthy(true);
            status.setDetail("未配置，跳过检测");
            return status;
        }
        try {
            // RocketMQ nameserver 默认无 HTTP 端口，检测 DNS/端口可达性
            String[] parts = rocketmqNameServer.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9876;
            try (java.net.Socket socket = new java.net.Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), 3000);
                status.setHealthy(true);
                status.setDetail("连接正常");
            }
        } catch (Exception e) {
            status.setHealthy(false);
            status.setDetail("连接失败: " + e.getMessage());
        }
        return status;
    }

    /**
     * R10: HIS 健康检测
     * <p>
     * 通过 HTTP 请求 HIS 系统健康检查端点，超时 3s。
     * 若未配置 health-check-url 则标记为 SKIP。
     */
    private ComponentStatus checkHis() {
        ComponentStatus status = new ComponentStatus();
        status.setName("HIS");
        if (hisHealthCheckUrl == null || hisHealthCheckUrl.isBlank()) {
            status.setHealthy(true);
            status.setDetail("未配置，跳过检测");
            return status;
        }
        try {
            org.springframework.http.ResponseEntity<String> response =
                restTemplate.exchange(hisHealthCheckUrl, org.springframework.http.HttpMethod.GET,
                    null, String.class);
            boolean ok = response.getStatusCode().is2xxSuccessful();
            status.setHealthy(ok);
            status.setDetail(ok ? "连接正常" : "状态码异常: " + response.getStatusCode());
        } catch (Exception e) {
            status.setHealthy(false);
            status.setDetail("连接失败(超时3s): " + e.getMessage());
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
