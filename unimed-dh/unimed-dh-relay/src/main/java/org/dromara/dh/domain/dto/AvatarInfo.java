package org.dromara.dh.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数字人形象信息 DTO
 *
 * <p>中转接口返回给前端的形象数据，preview_image 已拼接完整 URL</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人形象信息")
public class AvatarInfo {

    @Schema(description = "形象名称", example = "100_20250813_9437286436405_avatar")
    private String name;

    @Schema(description = "预览图片完整URL", example = "http://192.168.2.43:8011/data/avatars/100_20250813_9437286436405_avatar/full_imgs/00000000.png")
    private String previewImage;
}
