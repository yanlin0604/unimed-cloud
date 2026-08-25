package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChHealthMetricRecordVo;
import org.dromara.chronic.mapper.ChHealthMetricRecordMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.dromara.chronic.support.rule.WarningRuleEngine;
import org.dromara.chronic.utils.MetricValueUtils;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 健康指标记录服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChHealthMetricRecordServiceImpl implements IChHealthMetricRecordService {

    private final ChHealthMetricRecordMapper baseMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Override
    public Long reportMetric(ChHealthMetricRecordBo bo) {
        bo.setMetricType(WarningRuleEngine.normalizeMetricType(bo.getMetricType()));
        applyReferenceValues(bo);
        if (bo.getIsAbnormal() == null && bo.getReferenceValueMin() != null && bo.getReferenceValueMax() != null) {
            BigDecimal value = MetricValueUtils.extractPrimaryValue(bo.getMetricValue(), bo.getMetricType());
            if (value != null) {
                bo.setIsAbnormal(value.compareTo(bo.getReferenceValueMin()) < 0
                    || value.compareTo(bo.getReferenceValueMax()) > 0);
            }
        }
        ChHealthMetricRecord entity = MapstructUtils.convert(bo, ChHealthMetricRecord.class);
        baseMapper.insert(entity);
        return entity.getMetricId();
    }

    @Override
    public ChHealthMetricRecord getById(Long metricId) {
        return baseMapper.selectById(metricId);
    }

    @Override
    public ChHealthMetricRecordVo queryById(Long metricId) {
        return baseMapper.selectVoById(metricId);
    }

    @Override
    public TableDataInfo<ChHealthMetricRecordVo> queryPageList(ChHealthMetricRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChHealthMetricRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChHealthMetricRecord::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getMetricType()), ChHealthMetricRecord::getMetricType, bo.getMetricType());
        lqw.eq(StringUtils.isNotBlank(bo.getDataSource()), ChHealthMetricRecord::getDataSource, bo.getDataSource());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsAbnormal()), ChHealthMetricRecord::getIsAbnormal, bo.getIsAbnormal());
        lqw.orderByDesc(ChHealthMetricRecord::getCreateTime);
        Page<ChHealthMetricRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillPatientName(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChHealthMetricRecordVo> queryTrend(Long patientId, String metricType, Integer limit) {
        LambdaQueryWrapper<ChHealthMetricRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChHealthMetricRecord::getPatientId, patientId);
        lqw.eq(StringUtils.isNotBlank(metricType), ChHealthMetricRecord::getMetricType, metricType);
        lqw.orderByDesc(ChHealthMetricRecord::getCreateTime);
        lqw.last("LIMIT " + (limit != null ? limit : 30));
        return baseMapper.selectVoList(lqw);
    }

    @Override
    public List<ChHealthMetricRecordVo> queryLatest(Long patientId) {
        return baseMapper.selectLatestByPatientId(patientId);
    }

    @Override
    public Void updateMetric(ChHealthMetricRecordBo bo) {
        ChHealthMetricRecord entity = MapstructUtils.convert(bo, ChHealthMetricRecord.class);
        baseMapper.updateById(entity);
        return null;
    }

    @Override
    public Void deleteMetric(Long metricId) {
        baseMapper.deleteById(metricId);
        return null;
    }

    /**
     * 基于年龄/性别自适应参考值（简化实现，后续可扩展为配置表驱动）
     */
    private void applyReferenceValues(ChHealthMetricRecordBo bo) {
        if (bo.getReferenceValueMin() != null && bo.getReferenceValueMax() != null) {
            return;
        }
        switch (bo.getMetricType()) {
            case "BP_SYSTOLIC" -> {
                bo.setReferenceValueMin(new BigDecimal("90"));
                bo.setReferenceValueMax(new BigDecimal("140"));
            }
            case "BP_DIASTOLIC" -> {
                bo.setReferenceValueMin(new BigDecimal("60"));
                bo.setReferenceValueMax(new BigDecimal("90"));
            }
            case "BLOOD_GLUCOSE" -> {
                bo.setReferenceValueMin(new BigDecimal("3.9"));
                bo.setReferenceValueMax(new BigDecimal("6.1"));
            }
            case "HEART_RATE" -> {
                bo.setReferenceValueMin(new BigDecimal("60"));
                bo.setReferenceValueMax(new BigDecimal("100"));
            }
            case "SPO2" -> {
                bo.setReferenceValueMin(new BigDecimal("95"));
                bo.setReferenceValueMax(new BigDecimal("100"));
            }
            case "TEMPERATURE" -> {
                bo.setReferenceValueMin(new BigDecimal("36.0"));
                bo.setReferenceValueMax(new BigDecimal("37.3"));
            }
            default -> { /* 无预设参考值的指标类型，跳过 */ }
        }
    }

    private void fillPatientName(List<ChHealthMetricRecordVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream().map(ChHealthMetricRecordVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!patientIds.isEmpty()) {
            try {
                Map<Long, String> nameMap = patientProfileMapper.selectBatchIds(patientIds).stream()
                    .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
                list.forEach(v -> v.setPatientName(nameMap.get(v.getPatientId())));
            } catch (Exception e) { /* ignore */ }
        }
    }
}
