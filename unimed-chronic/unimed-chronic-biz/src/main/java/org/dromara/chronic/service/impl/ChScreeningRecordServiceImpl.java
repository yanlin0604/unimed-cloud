package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChScreeningRecordBo;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.chronic.domain.entity.ChScreeningRecord;
import org.dromara.chronic.domain.vo.ChScreeningRecordVo;
import org.dromara.chronic.mapper.ChScreeningBatchMapper;
import org.dromara.chronic.mapper.ChScreeningRecordMapper;
import org.dromara.chronic.service.IChScreeningRecordService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 义诊筛查记录服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChScreeningRecordServiceImpl implements IChScreeningRecordService {

    private final ChScreeningRecordMapper baseMapper;
    private final ChScreeningBatchMapper screeningBatchMapper;

    @Override
    public ChScreeningRecordVo saveRecord(ChScreeningRecordBo bo) {
        ChScreeningRecord existed = baseMapper.selectOne(
            Wrappers.<ChScreeningRecord>lambdaQuery().eq(ChScreeningRecord::getOfflineUuid, bo.getOfflineUuid())
        );
        if (ObjectUtil.isNotNull(existed)) {
            return MapstructUtils.convert(existed, ChScreeningRecordVo.class);
        }
        ChScreeningRecord entity = MapstructUtils.convert(bo, ChScreeningRecord.class);
        entity.setUploadTime(new Date());
        baseMapper.insert(entity);
        return MapstructUtils.convert(entity, ChScreeningRecordVo.class);
    }

    @Override
    public List<ChScreeningRecordVo> batchSave(List<ChScreeningRecordBo> list) {
        if (CollUtil.isEmpty(list)) {
            return List.of();
        }
        List<ChScreeningRecordVo> result = new ArrayList<>(list.size());
        for (ChScreeningRecordBo item : list) {
            result.add(saveRecord(item));
        }
        return result;
    }

    @Override
    public ChScreeningRecordVo queryById(Long recordId) {
        return baseMapper.selectVoById(recordId);
    }

    @Override
    public List<ChScreeningRecordVo> queryByBatchId(Long batchId) {
        return baseMapper.selectVoList(
            Wrappers.<ChScreeningRecord>lambdaQuery()
                .eq(ChScreeningRecord::getBatchId, batchId)
                .orderByDesc(ChScreeningRecord::getUploadTime)
        );
    }

    @Override
    public TableDataInfo<ChScreeningRecordVo> queryPageList(ChScreeningRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChScreeningRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getBatchId()), ChScreeningRecord::getBatchId, bo.getBatchId());
        lqw.like(StringUtils.isNotBlank(bo.getPatientName()), ChScreeningRecord::getPatientName, bo.getPatientName());
        lqw.like(StringUtils.isNotBlank(bo.getPhone()), ChScreeningRecord::getPhone, bo.getPhone());
        lqw.eq(StringUtils.isNotBlank(bo.getRiskLevel()), ChScreeningRecord::getRiskLevel, bo.getRiskLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getEnrollStatus()), ChScreeningRecord::getEnrollStatus, bo.getEnrollStatus());
        lqw.orderByDesc(ChScreeningRecord::getUploadTime);
        Page<ChScreeningRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillBatchNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    private void fillBatchNames(List<ChScreeningRecordVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> batchIds = list.stream().map(ChScreeningRecordVo::getBatchId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!batchIds.isEmpty()) {
            try {
                screeningBatchMapper.selectBatchIds(batchIds).forEach(b ->
                    list.stream().filter(v -> b.getBatchId().equals(v.getBatchId()))
                        .forEach(v -> v.setBatchName(b.getBatchName())));
            } catch (Exception e) { /* ignore */ }
        }
    }
}
