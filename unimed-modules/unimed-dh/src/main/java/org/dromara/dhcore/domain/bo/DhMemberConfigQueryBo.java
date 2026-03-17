package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 会员配置查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhMemberConfigQueryBo extends BaseEntity {

    /**
     * 关键�?     */
    private String keyword;

    /**
     * 会员等级
     */
    private String level;

    /**
     * 状�?     */
    private String status;

    /**
     * 开始时�?     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
