package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 说话状态查询请求 DTO
 *
 * <p>用于查询数字人是否正在说话的请求参数</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "说话状态查询请求")
public class SpeakingStatusRequest {

    @Schema(description = "会话ID", example = "123456")
    private String sessionid = "0";
}