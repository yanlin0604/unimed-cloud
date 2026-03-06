package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Python 数字人引擎 /get_avatars 接口原始响应 DTO
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
public class GetAvatarsResponse {

    private Integer code;

    private List<AvatarItem> data;

    @Data
    public static class AvatarItem {

        private String name;

        @JsonProperty("preview_image")
        private String previewImage;
    }
}
