package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 视频上传配置查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhVideoUploadConfigQueryBo extends BaseEntity {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 上传类型
     */
    private String type;

    /**
     * 状态
     */
    private String status;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
