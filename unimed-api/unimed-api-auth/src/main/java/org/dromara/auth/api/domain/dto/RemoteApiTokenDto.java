package org.dromara.auth.api.domain.dto;

import java.io.Serializable;

/**
 * 远程API Token响应DTO
 *
 * <p>符合OAuth2风格的Token响应格式</p>
 *
 * @param accessToken 访问令牌
 * @param tokenType   令牌类型（固定为Bearer）
 * @param expiresIn   过期时间（秒）
 * @param scope       授权范围
 * @author unimed
 * @since 2.5.1
 */
public record RemoteApiTokenDto(
    String accessToken,
    String tokenType,
    Long expiresIn,
    String scope
) implements Serializable {

    /**
     * 创建Bearer类型的Token响应
     *
     * @param accessToken 访问令牌
     * @param expiresIn   过期时间（秒）
     * @param scope       授权范围
     * @return RemoteApiTokenDto实例
     */
    public static RemoteApiTokenDto bearer(String accessToken, Long expiresIn, String scope) {
        return new RemoteApiTokenDto(accessToken, "Bearer", expiresIn, scope);
    }

    /**
     * 获取完整的Authorization头值
     *
     * @return Bearer {token}格式的字符串
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }
}