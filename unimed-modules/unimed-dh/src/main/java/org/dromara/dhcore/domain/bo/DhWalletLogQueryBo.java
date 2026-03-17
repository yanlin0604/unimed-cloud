package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 钱包流水查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhWalletLogQueryBo extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 流水类型
     */
    private String type;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
