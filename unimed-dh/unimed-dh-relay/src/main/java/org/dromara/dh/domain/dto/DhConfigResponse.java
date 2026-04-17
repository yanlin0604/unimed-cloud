package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数字人配置响应 DTO
 *
 * <p>保存数字人配置的响应结果</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人配置响应")
public class DhConfigResponse {

    @Schema(description = "操作是否成功", example = "true")
    private Boolean success;

    @Schema(description = "响应消息", example = "批量更新了 2 个配置项并已保存")
    private String message;

    @Schema(description = "更新的配置项数量", example = "2")
    @JsonProperty("updated_count")
    private Integer updatedCount;
}