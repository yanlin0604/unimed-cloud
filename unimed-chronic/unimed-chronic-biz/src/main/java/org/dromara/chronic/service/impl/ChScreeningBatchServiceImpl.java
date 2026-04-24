package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.common.helper.OrgNameHelper;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 义诊筛查批次服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChScreeningBatchServiceImpl implements IChScreeningBatchService {

    private final ChScreeningBatchMapper baseMapper;
    private final OrgNameHelper orgNameHelper;

    @Override
    public ChScreeningBatchVo queryById(Long batchId) {
        ChScreeningBatchVo vo = baseMapper.selectVoById(batchId);
        if (vo != null) {
            fillOrgName(vo);
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChScreeningBatchVo> queryPageList(ChScreeningBatchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChScreeningBatch> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getBatchName()), ChScreeningBatch::getBatchName, bo.getBatchName());
        lqw.eq(ObjectUtil.isNotNull(bo.getOrgId()), ChScreeningBatch::getOrgId, bo.getOrgId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChScreeningBatch::getStatus, bo.getStatus());
        lqw.orderByDesc(ChScreeningBatch::getCreateTime);
        Page<ChScreeningBatchVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        // 回填 orgName
        List<ChScreeningBatchVo> records = page.getRecords();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> orgIds = records.stream().map(ChScreeningBatchVo::getOrgId)
                .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
            if (!orgIds.isEmpty()) {
                try {
                    Map<Long, String> orgNameMap = orgNameHelper.batchGetOrgName(orgIds);
                    records.forEach(v -> v.setOrgName(orgNameMap.get(v.getOrgId())));
                } catch (Exception e) {
                    // 查询失败不影响主流程
                }
            }
        }
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

    /**
     * 单条筛查批次VO回填机构名称
     */
    private void fillOrgName(ChScreeningBatchVo vo) {
        if (vo == null || vo.getOrgId() == null) {
            return;
        }
        try {
            Map<Long, String> orgNameMap = orgNameHelper.batchGetOrgName(List.of(vo.getOrgId()));
            vo.setOrgName(orgNameMap.get(vo.getOrgId()));
        } catch (Exception e) {
            // 查询失败不影响主流程
        }
    }
}
