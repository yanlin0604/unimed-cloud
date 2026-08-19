package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChWebhookSubscriptionBo;
import org.dromara.chronic.domain.vo.ChWebhookSubscriptionVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * Webhook订阅 Service 接口
 *
 * @author unimed
 */
public interface IChWebhookSubscriptionService {

    /**
     * 查询Webhook订阅
     */
    ChWebhookSubscriptionVo queryById(Long subId);

    /**
     * 分页查询Webhook订阅列表
     */
    TableDataInfo<ChWebhookSubscriptionVo> queryPageList(ChWebhookSubscriptionBo bo, PageQuery pageQuery);

    /**
     * 查询所有启用的订阅列表
     */
    List<ChWebhookSubscriptionVo> queryActiveListByEventType(String eventType);

    /**
     * 注册/新增Webhook订阅
     */
    Long insertByBo(ChWebhookSubscriptionBo bo);

    /**
     * 修改Webhook订阅
     */
    Boolean updateByBo(ChWebhookSubscriptionBo bo);

    /**
     * 更新最后调用状态
     */
    Boolean updateInvokeStatus(Long subId, String status);

    /**
     * 校验并批量删除Webhook订阅
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
