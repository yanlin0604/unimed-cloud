package org.dromara.dh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * <p>提供基础的健康检查接口，用于验证数字人微服务的运行状态。</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@RestController
@RequestMapping("/api/v1/dh")
public class HealthController {

    /**
     * 健康检查接口
     * 
     * @return 服务状态信息
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        var healthInfo = Map.of(
            "status", "UP",
            "service", "unimed-dh",
            "description", "数字人微服务",
            "timestamp", LocalDateTime.now(),
            "version", "2.5.1"
        );
        
        return ResponseEntity.ok(healthInfo);
    }

    /**
     * 服务信息接口
     * 
     * @return 服务详细信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        var serviceInfo = Map.of(
            "name", "数字人微服务",
            "description", "专门负责封装 Python 数字人 API 调用，为其他服务提供统一的 REST 接口",
            "version", "2.5.1",
            "features", new String[]{
                "数字人实例管理",
                "配置管理",
                "服务启停控制",
                "容错机制",
                "监控能力"
            }
        );
        
        return ResponseEntity.ok(serviceInfo);
    }
}