package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDiseaseConfigBo;
import org.dromara.chronic.domain.entity.ChDiseaseConfig;
import org.dromara.chronic.domain.entity.ChIcdDict;
import org.dromara.chronic.domain.vo.ChDiseaseConfigVo;
import org.dromara.chronic.domain.vo.ChIcdDictVo;
import org.dromara.chronic.mapper.ChDiseaseConfigMapper;
import org.dromara.chronic.mapper.ChIcdDictMapper;
import org.dromara.chronic.service.IChDiseaseConfigService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 病种配置服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChDiseaseConfigServiceImpl implements IChDiseaseConfigService {

    private final ChDiseaseConfigMapper baseMapper;
    private final ChIcdDictMapper icdDictMapper;

    @Override
    public TableDataInfo<ChDiseaseConfigVo> queryPageList(ChDiseaseConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChDiseaseConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getDiseaseCode()), ChDiseaseConfig::getDiseaseCode, bo.getDiseaseCode());
        lqw.like(StringUtils.isNotBlank(bo.getDiseaseName()), ChDiseaseConfig::getDiseaseName, bo.getDiseaseName());
        lqw.eq(StringUtils.isNotBlank(bo.getDiseaseCategory()), ChDiseaseConfig::getDiseaseCategory, bo.getDiseaseCategory());
        lqw.eq(bo.getIsPrimary() != null, ChDiseaseConfig::getIsPrimary, bo.getIsPrimary());
        lqw.eq(bo.getIsActive() != null, ChDiseaseConfig::getIsActive, bo.getIsActive());
        lqw.orderByAsc(ChDiseaseConfig::getDiseaseCode);
        Page<ChDiseaseConfigVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public ChDiseaseConfigVo queryById(Long configId) {
        return baseMapper.selectVoById(configId);
    }

    @Override
    public Boolean insertByBo(ChDiseaseConfigBo bo) {
        ChDiseaseConfig entity = MapstructUtils.convert(bo, ChDiseaseConfig.class);
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public Boolean updateByBo(ChDiseaseConfigBo bo) {
        if (bo.getConfigId() == null) {
            throw new ServiceException("病种配置ID不能为空");
        }
        ChDiseaseConfig entity = MapstructUtils.convert(bo, ChDiseaseConfig.class);
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean disableById(Long configId) {
        ChDiseaseConfig entity = baseMapper.selectById(configId);
        if (entity == null) {
            throw new ServiceException("病种配置不存在");
        }
        entity.setIsActive(Boolean.FALSE);
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean deleteByIds(java.util.Collection<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public List<ChIcdDictVo> queryIcdList(String keyword) {
        LambdaQueryWrapper<ChIcdDict> lqw = Wrappers.lambdaQuery();
        lqw.and(StringUtils.isNotBlank(keyword), wrapper -> wrapper
            .like(ChIcdDict::getIcdCode, keyword)
            .or()
            .like(ChIcdDict::getIcdNameCn, keyword)
            .or()
            .like(ChIcdDict::getIcdNameEn, keyword));
        lqw.orderByAsc(ChIcdDict::getIcdCode);
        return icdDictMapper.selectVoList(lqw);
    }
}
