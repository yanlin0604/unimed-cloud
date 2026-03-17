package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 充值工单查询对�? */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhTopupQueryBo extends BaseEntity {

    /**
     * 关键�?     */
    private String keyword;

    /**
     * 工单状�?     */
    private String status;

    /**
     * 开始时�?     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
