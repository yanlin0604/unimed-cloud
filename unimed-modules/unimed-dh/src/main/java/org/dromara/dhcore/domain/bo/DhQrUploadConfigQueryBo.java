package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 收款码配置查询对�? */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhQrUploadConfigQueryBo extends BaseEntity {

    /**
     * 关键�?     */
    private String keyword;

    /**
     * 收款码类�?     */
    private String type;

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
