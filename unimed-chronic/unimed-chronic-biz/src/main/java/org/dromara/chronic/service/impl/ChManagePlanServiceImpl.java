package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChManagePlanBo;
import org.dromara.chronic.domain.bo.ChManagePlanItemBo;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.entity.ChManagePlanItem;
import org.dromara.chronic.domain.vo.ChManagePlanVo;
import org.dromara.chronic.mapper.ChManagePlanItemMapper;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.service.IChManagePlanService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理方案服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChManagePlanServiceImpl implements IChManagePlanService {

    private final ChManagePlanMapper managePlanMapper;
    private final ChManagePlanItemMapper managePlanItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(ChManagePlanBo bo) {
        ChManagePlan entity = MapstructUtils.convert(bo, ChManagePlan.class);
        if (entity.getPlanStatus() == null) {
            entity.setPlanStatus("DRAFT");
        }
        managePlanMapper.insert(entity);
        saveItems(entity.getPlanId(), bo.getItemList());
        return entity.getPlanId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlan(ChManagePlanBo bo) {
        if (bo.getPlanId() == null) {
            throw new ServiceException("方案ID不能为空");
        }
        ChManagePlan entity = MapstructUtils.convert(bo, ChManagePlan.class);
        boolean success = managePlanMapper.updateById(entity) > 0;
        if (success) {
            managePlanItemMapper.delete(Wrappers.<ChManagePlanItem>lambdaQuery().eq(ChManagePlanItem::getPlanId, bo.getPlanId()));
            saveItems(bo.getPlanId(), bo.getItemList());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enablePlan(Long planId) {
        ChManagePlan current = managePlanMapper.selectById(planId);
        if (current == null) {
            throw new ServiceException("管理方案不存在");
        }
        managePlanMapper.update(null,
            Wrappers.<ChManagePlan>lambdaUpdate()
                .eq(ChManagePlan::getPatientId, current.getPatientId())
                .eq(ChManagePlan::getDiseaseCode, current.getDiseaseCode())
                .eq(ChManagePlan::getPlanStatus, "ACTIVE")
                .set(ChManagePlan::getPlanStatus, "HISTORY")
        );
        current.setPlanStatus("ACTIVE");
        return managePlanMapper.updateById(current) > 0;
    }

    @Override
    public Boolean disablePlan(Long planId) {
        ChManagePlan current = managePlanMapper.selectById(planId);
        if (current == null) {
            throw new ServiceException("管理方案不存在");
        }
        current.setPlanStatus("DISABLED");
        return managePlanMapper.updateById(current) > 0;
    }

    @Override
    public List<ChManagePlanVo> queryByPatientId(Long patientId) {
        List<ChManagePlanVo> plans = managePlanMapper.selectVoList(
            Wrappers.<ChManagePlan>lambdaQuery()
                .eq(ChManagePlan::getPatientId, patientId)
                .orderByDesc(ChManagePlan::getCreateTime)
        );
        plans.forEach(plan -> plan.setItemList(
            managePlanItemMapper.selectVoList(
                Wrappers.<ChManagePlanItem>lambdaQuery().eq(ChManagePlanItem::getPlanId, plan.getPlanId())
            )
        ));
        return plans;
    }

    private void saveItems(Long planId, List<ChManagePlanItemBo> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        List<ChManagePlanItem> items = MapstructUtils.convert(itemList, ChManagePlanItem.class);
        items.forEach(item -> item.setPlanId(planId));
        managePlanItemMapper.insertBatch(items);
    }
}
