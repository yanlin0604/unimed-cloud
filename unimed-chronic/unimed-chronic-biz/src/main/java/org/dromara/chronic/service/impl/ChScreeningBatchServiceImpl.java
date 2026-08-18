package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.chronic.domain.entity.ChScreeningRecord;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.chronic.mapper.ChScreeningBatchMapper;
import org.dromara.chronic.mapper.ChScreeningRecordMapper;
import org.dromara.chronic.service.IChScreeningBatchService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 义诊筛查批次服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChScreeningBatchServiceImpl implements IChScreeningBatchService {

    /**
     * ch_screening_batch.status 合法取值（与 chronic_screening_status 字典一致）
     */
    private static final Set<String> VALID_STATUSES = Set.of("PLANNED", "ONGOING", "FINISHED", "CANCELED");

    private static final String STATUS_HINT = "，可选值：PLANNED/ONGOING/FINISHED/CANCELED";

    private final ChScreeningBatchMapper baseMapper;
    private final ChScreeningRecordMapper screeningRecordMapper;

    @Override
    public ChScreeningBatchVo queryById(Long batchId) {
        ChScreeningBatchVo vo = baseMapper.selectVoById(batchId);
        if (vo != null) {
            // doctorNickName 由 VO 上的 @Translation(USER_ID_TO_NICKNAME) 序列化时自动填充，此处只需补记录数
            fillRecordCount(List.of(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChScreeningBatchVo> queryPageList(ChScreeningBatchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChScreeningBatch> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBatchName()), ChScreeningBatch::getBatchName, bo.getBatchName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChScreeningBatch::getStatus, bo.getStatus());
        lqw.eq(ObjectUtil.isNotNull(bo.getDoctorUserId()), ChScreeningBatch::getDoctorUserId, bo.getDoctorUserId());
        lqw.orderByDesc(ChScreeningBatch::getCreateTime);
        Page<ChScreeningBatchVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillRecordCount(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public Boolean insertByBo(ChScreeningBatchBo bo) {
        if (StringUtils.isNotBlank(bo.getStatus())) {
            validateStatus(bo.getStatus());
        }
        ChScreeningBatch entity = MapstructUtils.convert(bo, ChScreeningBatch.class);
        boolean success = baseMapper.insert(entity) > 0;
        if (success) {
            bo.setBatchId(entity.getBatchId());
        }
        return success;
    }

    @Override
    public Boolean updateByBo(ChScreeningBatchBo bo) {
        if (ObjectUtil.isNull(bo.getBatchId())) {
            throw new ServiceException("批次ID不能为空");
        }
        if (StringUtils.isNotBlank(bo.getStatus())) {
            validateStatus(bo.getStatus());
        }
        requireBatch(bo.getBatchId());
        ChScreeningBatch entity = MapstructUtils.convert(bo, ChScreeningBatch.class);
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public Void updateStatus(Long batchId, String status) {
        validateStatus(status);
        requireBatch(batchId);
        ChScreeningBatch update = new ChScreeningBatch();
        update.setBatchId(batchId);
        update.setStatus(status);
        baseMapper.updateById(update);
        return null;
    }

    // ---------- private ----------

    private ChScreeningBatch requireBatch(Long batchId) {
        ChScreeningBatch existed = batchId == null ? null : baseMapper.selectById(batchId);
        if (existed == null) {
            throw new ServiceException("筛查批次不存在");
        }
        return existed;
    }

    private void validateStatus(String status) {
        if (StringUtils.isBlank(status) || !VALID_STATUSES.contains(status)) {
            throw new ServiceException("非法的批次状态：" + status + STATUS_HINT);
        }
    }

    /**
     * 批量回填批次内筛查记录数：按 batch_id 聚合 ch_screening_record 一次查询，避免 N+1。
     */
    private void fillRecordCount(List<ChScreeningBatchVo> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<Long> batchIds = records.stream()
            .map(ChScreeningBatchVo::getBatchId)
            .filter(ObjectUtil::isNotNull)
            .distinct()
            .toList();
        if (batchIds.isEmpty()) {
            return;
        }
        List<Map<String, Object>> rows = screeningRecordMapper.selectMaps(
            Wrappers.<ChScreeningRecord>query()
                .select("batch_id", "COUNT(*) AS cnt")
                .in("batch_id", batchIds)
                .groupBy("batch_id")
        );
        Map<Long, Long> countMap = new HashMap<>(rows.size());
        for (Map<String, Object> row : rows) {
            Object batchId = row.get("batch_id");
            Object cnt = row.get("cnt");
            if (batchId instanceof Number batchIdNum && cnt instanceof Number cntNum) {
                countMap.put(batchIdNum.longValue(), cntNum.longValue());
            }
        }
        records.forEach(vo -> vo.setRecordCount(countMap.getOrDefault(vo.getBatchId(), 0L)));
    }
}
