package org.dromara.auth.domain.vo;

import java.io.Serializable;
import java.util.List;

/**
 * API Token 验证结果 VO
 *
 * <p>用于返回 Token 验证的详细信息</p>
 *
 * @param valid            Token 是否有效
 * @param keyName          关联的 API Key 名称
 * @param remainingSeconds 剩余有效时间（秒）
 * @param allowedEndpoints 允许访问的端点列表
 * @author unimed
 * @since 2.5.1
 */
public record ApiTokenValidationVo(
    boolean valid,
    String keyName,
    Long remainingSeconds,
    List<String> allowedEndpoints
) implements Serializable {

    /**
     * 创建有效的验证结果
     *
     * @param keyName          API Key 名称
     * @param remainingSeconds 剩余有效时间
     * @param allowedEndpoints 允许访问的端点
     * @return 有效的验证结果
     */
    public static ApiTokenValidationVo valid(String keyName, Long remainingSeconds, List<String> allowedEndpoints) {
        return new ApiTokenValidationVo(true, keyName, remainingSeconds, allowedEndpoints);
    }

    /**
     * 创建无效的验证结果
     *
     * @return 无效的验证结果
     */
    public static ApiTokenValidationVo invalid() {
        return new ApiTokenValidationVo(false, null, null, null);
    }
}