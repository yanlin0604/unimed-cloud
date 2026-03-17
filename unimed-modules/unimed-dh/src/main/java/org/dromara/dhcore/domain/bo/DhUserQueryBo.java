package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 用户查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhUserQueryBo extends BaseEntity {

    /**
     * 关键字（用户�?手机号）
     */
    private String keyword;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 状态（0启用 1停用�?     */
    private String status;
}
