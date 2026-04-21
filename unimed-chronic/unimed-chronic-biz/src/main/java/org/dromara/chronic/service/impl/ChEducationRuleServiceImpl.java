package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChEducationRuleBo;
import org.dromara.chronic.domain.entity.ChEducationRule;
import org.dromara.chronic.domain.vo.ChEducationRuleVo;
import org.dromara.chronic.mapper.ChEducationRuleMapper;
import org.dromara.chronic.service.IChEducationRuleService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宣教规则服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChEducationRuleServiceImpl implements IChEducationRuleService {

    private final ChEducationRuleMapper baseMapper;

    @Override
    public Long add(ChEducationRuleBo bo) {
        ChEducationRule entity = MapstructUtils.convert(bo, ChEducationRule.class);
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
        baseMapper.insert(entity);
        return entity.getRuleId();
    }

    @Override
    public Void update(ChEducationRuleBo bo) {
        ChEducationRule existing = baseMapper.selectById(bo.getRuleId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("宣教规则不存在");
        }
        ChEducationRule entity = MapstructUtils.convert(bo, ChEducationRule.class);
        baseMapper.updateById(entity);
        return null;
    }

    @Override
    public ChEducationRuleVo queryById(Long ruleId) {
        return baseMapper.selectVoById(ruleId);
    }

    @Override
    public TableDataInfo<ChEducationRuleVo> queryPageList(ChEducationRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChEducationRule> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getIsActive()), ChEducationRule::getIsActive, bo.getIsActive());
        lqw.orderByDesc(ChEducationRule::getCreateTime);
        Page<ChEducationRuleVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChEducationRuleVo> queryActiveRules() {
        return baseMapper.selectVoList(
            Wrappers.<ChEducationRule>lambdaQuery()
                .eq(ChEducationRule::getIsActive, true)
                .orderByAsc(ChEducationRule::getRuleId)
        );
    }
}
