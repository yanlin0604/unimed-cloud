package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 通知模板查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhNotifyTemplateQueryBo extends BaseEntity {

    /**
     * 关键�?     */
    private String keyword;

    /**
     * 通知场景
     */
    private String scene;

    /**
     * 状�?     */
    private String status;
}
