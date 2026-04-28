package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.entity.ChScreeningBatch;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.chronic.mapper.ChScreeningBatchMapper;
import org.dromara.chronic.service.IChScreeningBatchService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
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
    public TableDataInfo<ChScreeningBatchVo> queryPageList(ChScreeningBatchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChScreeningBatch> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBatchName()), ChScreeningBatch::getBatchName, bo.getBatchName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChScreeningBatch::getStatus, bo.getStatus());
        lqw.orderByDesc(ChScreeningBatch::getCreateTime);
        Page<ChScreeningBatchVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
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
