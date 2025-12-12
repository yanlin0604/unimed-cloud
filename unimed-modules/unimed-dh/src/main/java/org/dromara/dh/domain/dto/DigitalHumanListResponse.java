package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 数字人列表查询响应
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人列表查询响应")
public class DigitalHumanListResponse {

    @Schema(description = "响应码", example = "200")
    private Integer code;

    @Schema(description = "响应消息", example = "查询成功")
    private String msg;

    @Schema(description = "总记录数", example = "80")
    private Long total;

    @Schema(description = "数字人列表")
    private List<DigitalHumanInfo> rows;
}