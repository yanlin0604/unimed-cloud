package org.dromara.dh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * <p>提供基础的健康检查接口，用于验证数字人微服务的运行状态。</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Tag(name = "数字人服务 - 健康检查", description = "提供数字人微服务的健康检查和服务信息接口")
@RestController
@RequestMapping("/api/v1/dh")
public class HealthController {

    /**
     * 健康检查接口
     * 
     * @return 服务状态信息
     */
    @Operation(summary = "健康检查", description = "检查数字人微服务的运行状态")
    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        var healthInfo = Map.<String, Object>of(
            "status", "UP",
            "service", "unimed-dh",
            "description", "数字人微服务",
            "timestamp", LocalDateTime.now().toString(),
            "version", "2.5.1"
        );
        
        return R.ok(healthInfo);
    }

    /**
     * 服务信息接口
     * 
     * @return 服务详细信息
     */
    @Operation(summary = "服务信息", description = "获取数字人微服务的详细信息，包括版本、功能特性等")
    @GetMapping("/info")
    public R<Map<String, Object>> info() {
        var serviceInfo = Map.<String, Object>of(
            "name", "数字人微服务",
            "description", "专门负责封装 Python 数字人 API 调用，为其他服务提供统一的 REST 接口",
            "version", "2.5.1",
            "features", List.of(
                "数字人实例管理",
                "配置管理",
                "服务启停控制",
                "容错机制",
                "监控能力"
            )
        );
        
        return R.ok(serviceInfo);
    }
}