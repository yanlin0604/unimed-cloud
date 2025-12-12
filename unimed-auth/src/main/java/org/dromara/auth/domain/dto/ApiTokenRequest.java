package org.dromara.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * API Token 请求 DTO
 *
 * <p>用于外部系统请求获取 Access Token</p>
 *
 * @param apiKey API Key，用于身份识别
 * @author unimed
 * @since 2.5.1
 */
public record ApiTokenRequest(
    @NotBlank(message = "API Key 不能为空")
    String apiKey
) implements Serializable {
}
