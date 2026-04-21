package org.dromara.chronic.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.chronic.mapper.ChScreeningBatchMapper;
import org.dromara.chronic.service.IChScreeningBatchService;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Service;

/**
 * 义诊筛查批次服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChScreeningBatchServiceImpl implements IChScreeningBatchService {

    private final ChScreeningBatchMapper baseMapper;

    @Override
    public ChScreeningBatchVo queryById(Long batchId) {
        return baseMapper.selectVoById(batchId);
    }

    @Override
    public Boolean insertByBo(ChScreeningBatchBo bo) {
        ChScreeningBatch entity = MapstructUtils.convert(bo, ChScreeningBatch.class);
        boolean success = baseMapper.insert(entity) > 0;
        if (success) {
            bo.setBatchId(entity.getBatchId());
        }
        return success;
    }
}
