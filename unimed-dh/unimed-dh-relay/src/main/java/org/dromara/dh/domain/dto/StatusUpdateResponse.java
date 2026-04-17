package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 状态修改响应
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "状态修改响应")
public class StatusUpdateResponse {

    @Schema(description = "响应消息")
    private String msg;

    @Schema(description = "响应码")
    private Integer code;
}