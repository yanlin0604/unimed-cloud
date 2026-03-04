package org.dromara.dh.domain.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Token验证结果VO
 *
 * <p>用于返回Token验证的详细信息</p>
 *
 * @param valid            Token是否有效
 * @param keyName          关联的API Key名称
 * @param remainingSeconds 剩余有效时间（秒）
 * @param allowedEndpoints 允许访问的端点列表
 * @author unimed
 * @since 2.5.1
 */
public record TokenValidationVo(
    boolean valid,
    String keyName,
    Long remainingSeconds,
    List<String> allowedEndpoints
) implements Serializable {

    /**
     * 创建有效的验证结果
     *
     * @param keyName          API Key名称
     * @param remainingSeconds 剩余有效时间
     * @param allowedEndpoints 允许访问的端点
     * @return 有效的验证结果
     */
    public static TokenValidationVo valid(String keyName, Long remainingSeconds, List<String> allowedEndpoints) {
        return new TokenValidationVo(true, keyName, remainingSeconds, allowedEndpoints);
    }

    /**
     * 创建无效的验证结果
     *
     * @return 无效的验证结果
     */
    public static TokenValidationVo invalid() {
        return new TokenValidationVo(false, null, null, null);
    }

    /**
     * 检查是否允许访问指定端点
     *
     * @param endpoint 端点路径
     * @return 是否允许访问
     */
    public boolean isEndpointAllowed(String endpoint) {
        if (!valid || allowedEndpoints == null || allowedEndpoints.isEmpty()) {
            return false;
        }

        return allowedEndpoints.stream()
            .anyMatch(pattern -> matchesPattern(endpoint, pattern));
    }

    /**
     * 模式匹配（支持通配符*）
     *
     * @param endpoint 端点路径
     * @param pattern  匹配模式
     * @return 是否匹配
     */
    private boolean matchesPattern(String endpoint, String pattern) {
        if (pattern.equals("*")) {
            return true;
        }

        if (pattern.contains("*")) {
            String regex = pattern.replace("*", ".*");
            return endpoint.matches(regex);
        }

        return endpoint.equals(pattern);
    }
}