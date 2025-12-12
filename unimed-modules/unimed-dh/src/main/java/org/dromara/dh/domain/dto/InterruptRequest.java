package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 打断请求 DTO
 *
 * <p>用于打断数字人当前说话的请求参数</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "打断请求")
public class InterruptRequest {

    @Schema(description = "会话ID", example = "123456")
    private String sessionid = "0";
}