package org.dromara.dhcore.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 数字人口播订单查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhOrderQueryBo extends BaseEntity {

    /**
     * 关键词（订单号/标题）
     */
    private String keyword;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 申请人姓名
     */
    private String applicantName;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;
}
