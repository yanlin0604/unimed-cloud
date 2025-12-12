package org.dromara.auth.api.domain.dto;

import java.io.Serializable;

/**
 * 远程API Token请求DTO
 *
 * <p>用于微服务间调用时的Token请求</p>
 *
 * @param apiKey API Key，用于身份识别
 * @author unimed
 * @since 2.5.1
 */
public record RemoteApiTokenRequest(
    String apiKey
) implements Serializable {

    /**
     * 创建Token请求
     *
     * @param apiKey API Key
     * @return RemoteApiTokenRequest实例
     */
    public static RemoteApiTokenRequest of(String apiKey) {
        return new RemoteApiTokenRequest(apiKey);
    }
}