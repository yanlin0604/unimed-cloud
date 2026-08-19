package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWebhookSubscriptionBo;
import org.dromara.chronic.domain.entity.ChWebhookSubscription;
import org.dromara.chronic.domain.vo.ChWebhookSubscriptionVo;
import org.dromara.chronic.mapper.ChWebhookSubscriptionMapper;
import org.dromara.chronic.service.IChWebhookSubscriptionService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Webhook订阅服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChWebhookSubscriptionServiceImpl implements IChWebhookSubscriptionService {

    private final ChWebhookSubscriptionMapper subscriptionMapper;

    @Override
    public ChWebhookSubscriptionVo queryById(Long subId) {
        return subscriptionMapper.selectVoById(subId);
    }

    @Override
    public TableDataInfo<ChWebhookSubscriptionVo> queryPageList(ChWebhookSubscriptionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChWebhookSubscription> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getThirdPartyName()), ChWebhookSubscription::getThirdPartyName, bo.getThirdPartyName());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsActive()), ChWebhookSubscription::getIsActive, bo.getIsActive());
        lqw.orderByDesc(ChWebhookSubscription::getCreateTime);
        Page<ChWebhookSubscriptionVo> page = subscriptionMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChWebhookSubscriptionVo> queryActiveListByEventType(String eventType) {
        LambdaQueryWrapper<ChWebhookSubscription> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChWebhookSubscription::getIsActive, 1);
        if (StringUtils.isNotBlank(eventType)) {
            lqw.like(ChWebhookSubscription::getEventTypes, eventType);
        }
        return subscriptionMapper.selectVoList(lqw);
    }

    @Override
    public Long insertByBo(ChWebhookSubscriptionBo bo) {
        ChWebhookSubscription entity = MapstructUtils.convert(bo, ChWebhookSubscription.class);
        if (entity.getIsActive() == null) {
            entity.setIsActive(1);
        }
        if (entity.getRetryMax() == null) {
            entity.setRetryMax(5);
        }
        if (StringUtils.isBlank(entity.getRetryStrategy())) {
            entity.setRetryStrategy("EXPONENTIAL_BACKOFF");
        }
        subscriptionMapper.insert(entity);
        return entity.getSubId();
    }

    @Override
    public Boolean updateByBo(ChWebhookSubscriptionBo bo) {
        ChWebhookSubscription entity = subscriptionMapper.selectById(bo.getSubId());
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("Webhook订阅不存在");
        }
        ChWebhookSubscription update = MapstructUtils.convert(bo, ChWebhookSubscription.class);
        subscriptionMapper.updateById(update);
        return true;
    }

    @Override
    public Boolean updateInvokeStatus(Long subId, String status) {
        ChWebhookSubscription entity = subscriptionMapper.selectById(subId);
        if (entity != null) {
            entity.setLastInvokeTime(new Date());
            entity.setLastInvokeStatus(status);
            subscriptionMapper.updateById(entity);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return subscriptionMapper.deleteByIds(ids) > 0;
    }
}
