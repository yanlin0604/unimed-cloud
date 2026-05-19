package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChManagePlanBo;
import org.dromara.chronic.domain.bo.ChManagePlanItemBo;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.entity.ChManagePlanItem;
import org.dromara.chronic.domain.vo.ChManagePlanItemVo;
import org.dromara.chronic.domain.vo.ChManagePlanVo;
import org.dromara.chronic.mapper.ChManagePlanItemMapper;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.service.IChManagePlanService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final DiseaseNameHelper diseaseNameHelper;

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
        plans.forEach(plan -> {
            List<ChManagePlanItemVo> items = managePlanItemMapper.selectVoList(
                Wrappers.<ChManagePlanItem>lambdaQuery().eq(ChManagePlanItem::getPlanId, plan.getPlanId())
            );
            plan.setItemList(items);
        });
        fillPlanNames(plans);
        return plans;
    }

    @Override
    public ChManagePlanVo queryCurrentPlan(Long patientId) {
        List<ChManagePlanVo> plans = managePlanMapper.selectVoList(
            Wrappers.<ChManagePlan>lambdaQuery()
                .eq(ChManagePlan::getPatientId, patientId)
                .eq(ChManagePlan::getPlanStatus, "ACTIVE")
                .last("LIMIT 1")
        );
        if (CollUtil.isEmpty(plans)) {
            return null;
        }
        ChManagePlanVo plan = plans.get(0);
        List<ChManagePlanItemVo> items = managePlanItemMapper.selectVoList(
            Wrappers.<ChManagePlanItem>lambdaQuery().eq(ChManagePlanItem::getPlanId, plan.getPlanId())
        );
        plan.setItemList(items);
        fillPlanNames(List.of(plan));
        return plan;
    }

    private void saveItems(Long planId, List<ChManagePlanItemBo> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        List<ChManagePlanItem> items = MapstructUtils.convert(itemList, ChManagePlanItem.class);
        items.forEach(item -> item.setPlanId(planId));
        managePlanItemMapper.insertBatch(items);
    }

    private void fillPlanNames(List<ChManagePlanVo> list) {
        if (CollUtil.isEmpty(list)) return;
        // diseaseName
        List<String> diseaseCodes = list.stream()
            .map(ChManagePlanVo::getDiseaseCode)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, String> diseaseNameMap = new HashMap<>();
        if (!diseaseCodes.isEmpty()) {
            try {
                diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
            } catch (Exception e) {
                /* ignore */
            }
        }
        // 表 ch_manage_plan 当前无 plan_name 字段，为避免前端展示"-"，用病种名兜底拼接
        Map<String, String> finalDiseaseNameMap = diseaseNameMap;
        list.forEach(v -> {
            String diseaseName = finalDiseaseNameMap.get(v.getDiseaseCode());
            v.setDiseaseName(diseaseName);
            if (StringUtils.isBlank(v.getPlanName())) {
                if (StringUtils.isNotBlank(diseaseName)) {
                    v.setPlanName(diseaseName + "管理方案");
                } else if (StringUtils.isNotBlank(v.getDiseaseCode())) {
                    v.setPlanName(v.getDiseaseCode() + "管理方案");
                }
            }
        });
    }
}
