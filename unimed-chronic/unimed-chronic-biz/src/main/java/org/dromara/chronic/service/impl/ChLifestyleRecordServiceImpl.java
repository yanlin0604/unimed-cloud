package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChLifestyleRecordBo;
import org.dromara.chronic.domain.entity.ChLifestyleRecord;
import org.dromara.chronic.domain.vo.ChLifestyleRecordVo;
import org.dromara.chronic.mapper.ChLifestyleRecordMapper;
import org.dromara.chronic.service.IChLifestyleRecordService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 生活方式记录服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChLifestyleRecordServiceImpl implements IChLifestyleRecordService {

    private final ChLifestyleRecordMapper baseMapper;

    @Override
    public Long add(ChLifestyleRecordBo bo) {
        ChLifestyleRecord entity = MapstructUtils.convert(bo, ChLifestyleRecord.class);
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Void update(ChLifestyleRecordBo bo) {
        if (bo.getId() == null || baseMapper.selectById(bo.getId()) == null) {
            throw new ServiceException("生活方式记录不存在");
        }
        ChLifestyleRecord entity = MapstructUtils.convert(bo, ChLifestyleRecord.class);
        baseMapper.updateById(entity);
        return null;
    }

    @Override
    public Void remove(Long id) {
        if (id == null || baseMapper.selectById(id) == null) {
            throw new ServiceException("生活方式记录不存在");
        }
        baseMapper.deleteById(id);
        return null;
    }

    @Override
    public ChLifestyleRecordVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<ChLifestyleRecordVo> queryPageList(ChLifestyleRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChLifestyleRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChLifestyleRecord::getPatientId, bo.getPatientId());
        lqw.orderByDesc(ChLifestyleRecord::getCreateTime);
        Page<ChLifestyleRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChLifestyleRecordVo> queryTrend(Long patientId, Integer limit) {
        return baseMapper.selectVoList(
            Wrappers.<ChLifestyleRecord>lambdaQuery()
                .eq(ChLifestyleRecord::getPatientId, patientId)
                .orderByDesc(ChLifestyleRecord::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 12))
        );
    }

    @Override
    public ChLifestyleRecordVo queryLatest(Long patientId) {
        List<ChLifestyleRecordVo> list = baseMapper.selectVoList(
            Wrappers.<ChLifestyleRecord>lambdaQuery()
                .eq(ChLifestyleRecord::getPatientId, patientId)
                .orderByDesc(ChLifestyleRecord::getCreateTime)
                .last("LIMIT 1")
        );
        return list.isEmpty() ? null : list.get(0);
    }
}
