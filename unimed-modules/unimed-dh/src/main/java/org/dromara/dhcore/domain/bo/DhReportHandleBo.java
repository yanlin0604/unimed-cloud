package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 举报处理对象
 */
@Data
public class DhReportHandleBo {

    /**
     * 举报ID
     */
    @NotNull(message = "举报ID不能为空")
    private Long reportId;

    /**
     * 处理结果（CONFIRMED/DISMISSED�?     */
    @NotBlank(message = "处理结果不能为空")
    private String result;

    /**
     * 处理说明
     */
    @NotBlank(message = "处理说明不能为空")
    private String handleResult;
}
