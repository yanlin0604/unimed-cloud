package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthEducationContentBo;
import org.dromara.chronic.domain.entity.ChHealthEducationContent;
import org.dromara.chronic.domain.entity.ChHealthEducationDelivery;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.domain.vo.ChHealthEducationDeliveryVo;
import org.dromara.chronic.mapper.ChHealthEducationContentMapper;
import org.dromara.chronic.mapper.ChHealthEducationDeliveryMapper;
import org.dromara.chronic.service.IChHealthEducationService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 宣教服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChHealthEducationServiceImpl implements IChHealthEducationService {

    private final ChHealthEducationContentMapper contentMapper;
    private final ChHealthEducationDeliveryMapper deliveryMapper;

    @Override
    public Long createContent(ChHealthEducationContentBo bo) {
        ChHealthEducationContent entity = MapstructUtils.convert(bo, ChHealthEducationContent.class);
        contentMapper.insert(entity);
        return entity.getContentId();
    }

    @Override
    public ChHealthEducationContentVo queryContentById(Long contentId) {
        return contentMapper.selectVoById(contentId);
    }

    @Override
    public TableDataInfo<ChHealthEducationContentVo> queryContentPageList(ChHealthEducationContentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChHealthEducationContent> lqw = Wrappers.lambdaQuery();
        lqw.like(ObjectUtil.isNotNull(bo.getTitle()), ChHealthEducationContent::getTitle, bo.getTitle());
        lqw.orderByDesc(ChHealthEducationContent::getCreateTime);
        Page<ChHealthEducationContentVo> page = contentMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChHealthEducationDeliveryVo> queryDeliveriesByPatientId(Long patientId) {
        return deliveryMapper.selectVoList(
            Wrappers.<ChHealthEducationDelivery>lambdaQuery()
                .eq(ChHealthEducationDelivery::getPatientId, patientId)
                .orderByDesc(ChHealthEducationDelivery::getCreateTime)
        );
    }

    @Override
    public Void markRead(Long deliveryId, Integer stayDuration) {
        ChHealthEducationDelivery delivery = deliveryMapper.selectById(deliveryId);
        if (ObjectUtil.isNull(delivery)) {
            throw new ServiceException("宣教投递记录不存在");
        }
        delivery.setReadStatus(true);
        delivery.setReadTime(new Date());
        if (stayDuration != null) {
            delivery.setStayDuration(stayDuration);
        }
        deliveryMapper.updateById(delivery);
        return null;
    }
}
