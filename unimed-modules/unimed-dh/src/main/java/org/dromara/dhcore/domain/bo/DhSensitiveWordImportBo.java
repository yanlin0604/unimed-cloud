package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 敏感词批量导入对象
 */
@Data
public class DhSensitiveWordImportBo {

    /**
     * 敏感词列表
     */
    @NotEmpty(message = "导入词列表不能为空")
    private List<String> words;
}
