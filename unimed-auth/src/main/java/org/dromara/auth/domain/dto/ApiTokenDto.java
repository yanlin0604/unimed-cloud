package org.dromara.auth.domain.dto;

import java.io.Serializable;

/**
 * API Token 响应 DTO
 *
 * <p>符合 OAuth2 风格的 Token 响应格式</p>
 *
 * @param accessToken 访问令牌
 * @param tokenType   令牌类型（固定为 Bearer）
 * @param expiresIn   过期时间（秒）
 * @param scope       授权范围
 * @author unimed
 * @since 2.5.1
 */
public record ApiTokenDto(
    String accessToken,
    String tokenType,
    Long expiresIn,
    String scope
) implements Serializable {

    /**
     * 创建 Bearer 类型的 Token 响应
     *
     * @param accessToken 访问令牌
     * @param expiresIn   过期时间（秒）
     * @param scope       授权范围
     * @return ApiTokenDto 实例
     */
    public static ApiTokenDto bearer(String accessToken, Long expiresIn, String scope) {
        return new ApiTokenDto(accessToken, "Bearer", expiresIn, scope);
    }
}
