package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientTagDictBo;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.entity.ChPatientTagDict;
import org.dromara.chronic.domain.vo.ChPatientTagDictVo;
import org.dromara.chronic.mapper.ChPatientTagDictMapper;
import org.dromara.chronic.mapper.ChPatientTagMapper;
import org.dromara.chronic.service.IChPatientTagDictService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 患者标签字典服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChPatientTagDictServiceImpl implements IChPatientTagDictService {

    private final ChPatientTagDictMapper baseMapper;
    private final ChPatientTagMapper patientTagMapper;

    @Override
    public TableDataInfo<ChPatientTagDictVo> queryPageList(ChPatientTagDictBo bo, PageQuery pageQuery) {
        Page<ChPatientTagDictVo> page = baseMapper.selectVoPage(pageQuery.build(), buildWrapper(bo));
        fillUseCount(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChPatientTagDictVo> queryList(ChPatientTagDictBo bo) {
        if (bo == null) {
            bo = new ChPatientTagDictBo();
        }
        if (StringUtils.isBlank(bo.getStatus())) {
            bo.setStatus("0");
        }
        return baseMapper.selectVoList(buildWrapper(bo));
    }

    @Override
    public ChPatientTagDictVo queryById(Long id) {
        ChPatientTagDictVo vo = baseMapper.selectVoById(id);
        if (vo != null) {
            fillUseCount(CollUtil.newArrayList(vo));
        }
        return vo;
    }

    @Override
    public Boolean insertByBo(ChPatientTagDictBo bo) {
        validateCodeUnique(bo.getTagCode(), null);
        ChPatientTagDict entity = MapstructUtils.convert(bo, ChPatientTagDict.class);
        if (entity != null && StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus("0");
        }
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public Boolean updateByBo(ChPatientTagDictBo bo) {
        if (bo.getId() == null) {
            throw new ServiceException("标签字典ID不能为空");
        }
        validateCodeUnique(bo.getTagCode(), bo.getId());
        ChPatientTagDict entity = MapstructUtils.convert(bo, ChPatientTagDict.class);
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean changeStatus(Long id, String status) {
        ChPatientTagDict entity = baseMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("标签字典不存在");
        }
        entity.setStatus(status);
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean deleteByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Boolean.FALSE;
        }
        List<ChPatientTagDict> dicts = baseMapper.selectByIds(ids);
        if (CollUtil.isEmpty(dicts)) {
            return Boolean.FALSE;
        }
        List<String> codes = dicts.stream().map(ChPatientTagDict::getTagCode).toList();
        Long inUse = patientTagMapper.selectCount(
            Wrappers.<ChPatientTag>lambdaQuery().in(ChPatientTag::getTagCode, codes)
        );
        if (inUse != null && inUse > 0) {
            throw new ServiceException("存在被引用的标签，无法删除（请先停用或解除患者绑定）");
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    private LambdaQueryWrapper<ChPatientTagDict> buildWrapper(ChPatientTagDictBo bo) {
        LambdaQueryWrapper<ChPatientTagDict> lqw = Wrappers.lambdaQuery();
        if (bo == null) {
            return lqw.orderByAsc(ChPatientTagDict::getSortOrder);
        }
        lqw.and(StringUtils.isNotBlank(bo.getKeyword()), w -> w
            .like(ChPatientTagDict::getTagCode, bo.getKeyword())
            .or().like(ChPatientTagDict::getTagName, bo.getKeyword()));
        lqw.eq(StringUtils.isNotBlank(bo.getTagType()), ChPatientTagDict::getTagType, bo.getTagType());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), ChPatientTagDict::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChPatientTagDict::getStatus, bo.getStatus());
        lqw.orderByAsc(ChPatientTagDict::getSortOrder).orderByDesc(ChPatientTagDict::getCreateTime);
        return lqw;
    }

    private void validateCodeUnique(String tagCode, Long excludeId) {
        if (StringUtils.isBlank(tagCode)) {
            return;
        }
        LambdaQueryWrapper<ChPatientTagDict> lqw = Wrappers.<ChPatientTagDict>lambdaQuery()
            .eq(ChPatientTagDict::getTagCode, tagCode);
        if (excludeId != null) {
            lqw.ne(ChPatientTagDict::getId, excludeId);
        }
        if (baseMapper.exists(lqw)) {
            throw new ServiceException("标签编码已存在：" + tagCode);
        }
    }

    /**
     * 批量填充 useCount。
     * 实现：按 tag_code 聚合 ch_patient_tag 计数，再回填到 VO。
     */
    private void fillUseCount(List<ChPatientTagDictVo> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<String> codes = records.stream()
            .map(ChPatientTagDictVo::getTagCode)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (codes.isEmpty()) {
            return;
        }
        List<Map<String, Object>> rows = patientTagMapper.selectMaps(
            Wrappers.<ChPatientTag>query()
                .select("tag_code", "COUNT(*) AS cnt")
                .in("tag_code", codes)
                .groupBy("tag_code")
        );
        Map<String, Long> countMap = new HashMap<>(rows.size());
        for (Map<String, Object> row : rows) {
            Object code = row.get("tag_code");
            Object cnt = row.get("cnt");
            if (code != null && cnt instanceof Number n) {
                countMap.put(code.toString(), n.longValue());
            }
        }
        records.forEach(vo -> vo.setUseCount(countMap.getOrDefault(vo.getTagCode(), 0L)));
    }
}
