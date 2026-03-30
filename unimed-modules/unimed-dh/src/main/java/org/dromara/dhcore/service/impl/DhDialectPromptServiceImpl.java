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
import org.dromara.dhcore.domain.bo.DhDialectPromptImportBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptQueryBo;
import org.dromara.dhcore.domain.convert.DhDialectConvert;
import org.dromara.dhcore.domain.entity.DhDialectPrompt;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;
import org.dromara.dhcore.mapper.DhDialectPromptMapper;
import org.dromara.dhcore.service.IDhDialectPromptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        lqw.orderByAsc(DhDialectPrompt::getSortOrder).orderByDesc(DhDialectPrompt::getCreateTime);
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
        // 自动计算排序号：取同分类最大排序号 + 1
        prompt.setSortOrder(bo.getSortOrder() != null ? bo.getSortOrder() : getMaxSortOrder(bo.getCategory()) + 1);
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
        uw.set(bo.getSortOrder() != null, DhDialectPrompt::getSortOrder, bo.getSortOrder());
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
        lqw.orderByAsc(DhDialectPrompt::getSortOrder);
        return dialectPromptMapper.selectList(lqw).stream()
            .map(DhDialectConvert::toDialectPromptVo).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> batchImport(DhDialectPromptImportBo bo) {
        int imported = 0;
        int duplicated = 0;
        String category = StringUtils.isNotBlank(bo.getCategory()) ? bo.getCategory() : "未分类";
        // 获取当前分类最大排序号，后续递增
        int currentMaxSort = getMaxSortOrder(category);
        for (String content : bo.getContents()) {
            if (StringUtils.isBlank(content)) {
                continue;
            }
            String trimContent = content.trim();
            // 跳过超过500字的内容
            if (trimContent.length() > 500) {
                continue;
            }
            // 检查重复
            DhDialectPrompt exists = dialectPromptMapper.selectOne(
                Wrappers.<DhDialectPrompt>lambdaQuery().eq(DhDialectPrompt::getContent, trimContent)
            );
            if (exists != null) {
                duplicated++;
                continue;
            }
            DhDialectPrompt entity = new DhDialectPrompt();
            entity.setContent(trimContent);
            entity.setCategory(category);
            entity.setStatus("0");
            entity.setSortOrder(++currentMaxSort);
            dialectPromptMapper.insert(entity);
            imported++;
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("imported", imported);
        result.put("duplicated", duplicated);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustSort(Long promptId, String direction) {
        DhDialectPrompt current = dialectPromptMapper.selectById(promptId);
        if (current == null) {
            throw new ServiceException("提示文字不存在");
        }

        // 查询同分类的所有数据，按序号排序
        LambdaQueryWrapper<DhDialectPrompt> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhDialectPrompt::getCategory, current.getCategory());
        lqw.orderByAsc(DhDialectPrompt::getSortOrder).orderByDesc(DhDialectPrompt::getCreateTime);
        List<DhDialectPrompt> list = new ArrayList<>(dialectPromptMapper.selectList(lqw));

        // 找到当前位置
        int currentIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPromptId().equals(promptId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            return;
        }

        // 根据方向确定目标位置
        int targetIndex;
        if ("up".equals(direction)) {
            if (currentIndex <= 0) {
                throw new ServiceException("已经是第一条了");
            }
            targetIndex = currentIndex - 1;
        } else if ("down".equals(direction)) {
            if (currentIndex >= list.size() - 1) {
                throw new ServiceException("已经是最后一条了");
            }
            targetIndex = currentIndex + 1;
        } else {
            throw new ServiceException("方向参数无效");
        }

        // 交换列表位置后重新编号，避免相同序号交换无效的问题
        Collections.swap(list, currentIndex, targetIndex);
        for (int i = 0; i < list.size(); i++) {
            DhDialectPrompt item = list.get(i);
            int newOrder = i + 1;
            if (!Integer.valueOf(newOrder).equals(item.getSortOrder())) {
                item.setSortOrder(newOrder);
                dialectPromptMapper.updateById(item);
            }
        }
    }

    /**
     * 获取指定分类的最大排序号
     */
    private int getMaxSortOrder(String category) {
        LambdaQueryWrapper<DhDialectPrompt> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(category), DhDialectPrompt::getCategory, category);
        lqw.orderByDesc(DhDialectPrompt::getSortOrder);
        lqw.last("LIMIT 1");
        DhDialectPrompt maxItem = dialectPromptMapper.selectOne(lqw);
        return maxItem != null && maxItem.getSortOrder() != null ? maxItem.getSortOrder() : 0;
    }
}
