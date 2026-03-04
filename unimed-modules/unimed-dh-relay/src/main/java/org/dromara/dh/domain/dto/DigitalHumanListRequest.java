package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 数字人列表查询请求
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人列表查询请求")
public class DigitalHumanListRequest {

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

//    @Schema(description = "数字人名称关键词", example = "护士")
//    private String keyword;
//
//    @Schema(description = "性别筛选", example = "female", allowableValues = {"male", "female"})
//    private String sex;
//
//    @Schema(description = "分组类别", example = "医护")
//    private String groupCategory;
}
