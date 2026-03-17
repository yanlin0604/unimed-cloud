package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 敏感词配置提交对�? */
@Data
public class DhSensitiveWordBo {

    /**
     * 敏感词ID
     */
    private Long id;

    /**
     * 敏感�?     */
    @NotBlank(message = "敏感词不能为�?)
    private String word;

    /**
     * 风险等级
     */
    @NotBlank(message = "风险等级不能为空")
    private String level;

    /**
     * 分类
     */
    @NotBlank(message = "分类不能为空")
    private String category;

    /**
     * 状�?     */
    @NotBlank(message = "状态不能为�?)
    private String status;
}
