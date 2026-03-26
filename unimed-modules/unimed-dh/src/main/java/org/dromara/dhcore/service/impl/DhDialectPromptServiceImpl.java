package org.dromara.dhcore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptQueryBo;
import org.dromara.dhcore.domain.convert.DhDialectConvert;
import org.dromara.dhcore.domain.entity.DhDialectPrompt;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;
import org.dromara.dhcore.mapper.DhDialectPromptMapper;
import org.dromara.dhcore.service.IDhDialectPromptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 方言采集提示文字服务实现类
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhDialectPromptServiceImpl implements IDhDialectPromptService {

    private final DhDialectPromptMapper dialectPromptMapper;

    @Override
    public TableDataInfo<DhDialectPromptVo> queryPage(DhDialectPromptQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhDialectPrompt> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getContent()), DhDialectPrompt::getContent, bo.getContent());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), DhDialectPrompt::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhDialectPrompt::getStatus, bo.getStatus());
        lqw.orderByDesc(DhDialectPrompt::getCreateTime);
        Page<DhDialectPrompt> page = dialectPromptMapper.selectPage(pageQuery.build(), lqw);
        List<DhDialectPromptVo> voList = page.getRecords().stream()
            .map(DhDialectConvert::toDialectPromptVo).toList();
        return new TableDataInfo<>(voList, page.getTotal());
    }

    @Override
    public DhDialectPromptVo save(DhDialectPromptBo bo) {
        DhDialectPrompt prompt = new DhDialectPrompt();
        prompt.setContent(bo.getContent());
        prompt.setCategory(bo.getCategory());
        prompt.setStatus(StringUtils.isNotBlank(bo.getStatus()) ? bo.getStatus() : "0");
        prompt.setRemark(bo.getRemark());
        dialectPromptMapper.insert(prompt);
        return DhDialectConvert.toDialectPromptVo(prompt);
    }

    @Override
    public DhDialectPromptVo update(DhDialectPromptBo bo) {
        if (bo.getPromptId() == null) {
            throw new ServiceException("提示文字ID不能为空");
        }
        DhDialectPrompt prompt = dialectPromptMapper.selectById(bo.getPromptId());
        if (prompt == null) {
            throw new ServiceException("提示文字不存在");
        }
        LambdaUpdateWrapper<DhDialectPrompt> uw = Wrappers.lambdaUpdate();
        uw.eq(DhDialectPrompt::getPromptId, bo.getPromptId());
        uw.set(StringUtils.isNotBlank(bo.getContent()), DhDialectPrompt::getContent, bo.getContent());
        uw.set(StringUtils.isNotBlank(bo.getCategory()), DhDialectPrompt::getCategory, bo.getCategory());
        uw.set(StringUtils.isNotBlank(bo.getStatus()), DhDialectPrompt::getStatus, bo.getStatus());
        uw.set(bo.getRemark() != null, DhDialectPrompt::getRemark, bo.getRemark());
        dialectPromptMapper.update(null, uw);
        return DhDialectConvert.toDialectPromptVo(dialectPromptMapper.selectById(bo.getPromptId()));
    }

    @Override
    public Boolean delete(List<Long> promptIds) {
        if (promptIds == null || promptIds.isEmpty()) {
            throw new ServiceException("请选择要删除的提示文字");
        }
        return dialectPromptMapper.deleteByIds(promptIds) > 0;
    }

    @Override
    public DhDialectPromptVo changeStatus(DhConfigStatusBo bo) {
        DhDialectPrompt prompt = dialectPromptMapper.selectById(bo.getId());
        if (prompt == null) {
            throw new ServiceException("提示文字不存在");
        }
        LambdaUpdateWrapper<DhDialectPrompt> uw = Wrappers.lambdaUpdate();
        uw.eq(DhDialectPrompt::getPromptId, bo.getId());
        uw.set(DhDialectPrompt::getStatus, bo.getStatus());
        dialectPromptMapper.update(null, uw);
        prompt.setStatus(bo.getStatus());
        return DhDialectConvert.toDialectPromptVo(prompt);
    }

    @Override
    public List<DhDialectPromptVo> listEnabled() {
        LambdaQueryWrapper<DhDialectPrompt> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhDialectPrompt::getStatus, "0");
        lqw.orderByDesc(DhDialectPrompt::getCreateTime);
        return dialectPromptMapper.selectList(lqw).stream()
            .map(DhDialectConvert::toDialectPromptVo).toList();
    }
}
