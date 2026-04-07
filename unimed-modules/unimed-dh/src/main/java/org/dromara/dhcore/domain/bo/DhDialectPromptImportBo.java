package org.dromara.dhcore.domain.bo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 方言采集提示文字批量导入对象
 *
 * @author unimed
 */
@Data
public class DhDialectPromptImportBo {

    /**
     * 提示文字列表
     */
    @NotEmpty(message = "导入内容不能为空")
    private List<String> contents;

}
