package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 敏感词查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhSensitiveWordQueryBo extends BaseEntity {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 风险等级
     */
    private String level;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态
     */
    private String status;
}
