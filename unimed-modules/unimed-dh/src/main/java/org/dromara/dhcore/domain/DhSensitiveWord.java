package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播敏感词配置对象 dh_sensitive_word
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_sensitive_word")
public class DhSensitiveWord extends TenantEntity {

    /**
     * 敏感词ID
     */
    @TableId(value = "word_id")
    private Long wordId;

    /**
     * 敏感词
     */
    private String word;

    /**
     * 风险等级
     */
    private String level;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
