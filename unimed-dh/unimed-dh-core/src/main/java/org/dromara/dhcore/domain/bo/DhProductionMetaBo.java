package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 保存生成元信息请求
 */
@Data
public class DhProductionMetaBo {

    /**
     * 生成渠道
     */
    @NotBlank(message = "生成渠道不能为空")
    private String generationChannel;

    /**
     * 生成引用标识（如第三方任务ID）
     */
    private String generationRef;

    /**
     * 操作人姓名
     */
    @NotBlank(message = "操作人不能为空")
    private String operatorName;

    /**
     * 备注
     */
    private String remark;
}
