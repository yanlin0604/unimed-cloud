package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 视频上传配置提交对象
 */
@Data
public class DhVideoUploadConfigBo {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    /**
     * 上传类型
     */
    @NotBlank(message = "上传类型不能为空")
    private String type;

    /**
     * 文件ID列表
     */
    private String videoFileIds;

    /**
     * 大小限制MB
     */
    @NotNull(message = "大小限制不能为空")
    private Integer maxSizeMb;

    /**
     * 格式描述
     */
    @NotBlank(message = "格式描述不能为空")
    private String formatDesc;

    /**
     * 状�?     */
    @NotBlank(message = "状态不能为�?)
    private String status;

    /**
     * 备注
     */
    private String remark;
}
