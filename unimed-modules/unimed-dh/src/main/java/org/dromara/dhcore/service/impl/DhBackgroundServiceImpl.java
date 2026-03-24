package org.dromara.dhcore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.DhBackground;
import org.dromara.dhcore.domain.bo.DhBackgroundBo;
import org.dromara.dhcore.domain.bo.DhBackgroundQueryBo;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.vo.DhBackgroundVo;
import org.dromara.dhcore.domain.vo.portal.PortalBackgroundVo;
import org.dromara.dhcore.mapper.DhBackgroundMapper;
import org.dromara.dhcore.service.IDhBackgroundService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数字人背景资源服务实现类
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhBackgroundServiceImpl implements IDhBackgroundService {

    private final DhBackgroundMapper dhBackgroundMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @Override
    public TableDataInfo<DhBackgroundVo> queryPage(DhBackgroundQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhBackground> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), DhBackground::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getBgType()), DhBackground::getBgType, bo.getBgType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhBackground::getStatus, bo.getStatus());
        lqw.orderByAsc(DhBackground::getSortOrder).orderByDesc(DhBackground::getCreateTime);
        Page<DhBackground> page = dhBackgroundMapper.selectPage(pageQuery.build(), lqw);
        List<DhBackground> records = page.getRecords();
        fillPreviewUrls(records);
        List<DhBackgroundVo> voList = records.stream().map(DhConvertUtils::toBackgroundVo).toList();
        return new TableDataInfo<>(voList, page.getTotal());
    }

    @Override
    public DhBackgroundVo save(DhBackgroundBo bo) {
        DhBackground background = new DhBackground();
        background.setName(bo.getName());
        background.setBgType(bo.getBgType());
        background.setOssId(bo.getOssId());
        background.setPreviewUrl(bo.getPreviewUrl());
        background.setIsSystem(1);
        background.setStatus("0");
        background.setSortOrder(bo.getSortOrder() != null ? bo.getSortOrder() : 0);
        background.setRemark(bo.getRemark());
        dhBackgroundMapper.insert(background);
        return DhConvertUtils.toBackgroundVo(background);
    }

    @Override
    public DhBackgroundVo update(DhBackgroundBo bo) {
        if (bo.getBackgroundId() == null) {
            throw new ServiceException("背景ID不能为空");
        }
        DhBackground background = dhBackgroundMapper.selectById(bo.getBackgroundId());
        if (background == null) {
            throw new ServiceException("背景不存在");
        }
        LambdaUpdateWrapper<DhBackground> uw = Wrappers.lambdaUpdate();
        uw.eq(DhBackground::getBackgroundId, bo.getBackgroundId());
        uw.set(StringUtils.isNotBlank(bo.getName()), DhBackground::getName, bo.getName());
        uw.set(StringUtils.isNotBlank(bo.getOssId()), DhBackground::getOssId, bo.getOssId());
        uw.set(StringUtils.isNotBlank(bo.getPreviewUrl()), DhBackground::getPreviewUrl, bo.getPreviewUrl());
        uw.set(bo.getSortOrder() != null, DhBackground::getSortOrder, bo.getSortOrder());
        uw.set(StringUtils.isNotBlank(bo.getRemark()), DhBackground::getRemark, bo.getRemark());
        dhBackgroundMapper.update(null, uw);
        return DhConvertUtils.toBackgroundVo(dhBackgroundMapper.selectById(bo.getBackgroundId()));
    }

    @Override
    public Boolean delete(Long backgroundId) {
        DhBackground background = dhBackgroundMapper.selectById(backgroundId);
        if (background == null) {
            throw new ServiceException("背景不存在");
        }
        return dhBackgroundMapper.deleteById(backgroundId) > 0;
    }

    @Override
    public DhBackgroundVo changeStatus(DhConfigStatusBo bo) {
        DhBackground background = dhBackgroundMapper.selectById(bo.getId());
        if (background == null) {
            throw new ServiceException("背景不存在");
        }
        LambdaUpdateWrapper<DhBackground> uw = Wrappers.lambdaUpdate();
        uw.eq(DhBackground::getBackgroundId, bo.getId());
        uw.set(DhBackground::getStatus, bo.getStatus());
        dhBackgroundMapper.update(null, uw);
        background.setStatus(bo.getStatus());
        return DhConvertUtils.toBackgroundVo(background);
    }

    @Override
    public List<PortalBackgroundVo> listEnabled() {
        LambdaQueryWrapper<DhBackground> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhBackground::getStatus, "0");
        lqw.orderByAsc(DhBackground::getSortOrder);
        List<DhBackground> backgrounds = dhBackgroundMapper.selectList(lqw);
        fillPreviewUrls(backgrounds);
        List<PortalBackgroundVo> result = new ArrayList<>();
        for (DhBackground bg : backgrounds) {
            result.add(DhConvertUtils.toPortalBackgroundVo(bg));
        }
        return result;
    }

    /**
     * 批量填充背景预览 URL（通过 OSS 服务获取预签名 URL）
     *
     * @param backgrounds 背景列表
     */
    private void fillPreviewUrls(List<DhBackground> backgrounds) {
        List<String> ossIds = backgrounds.stream()
            .map(DhBackground::getOssId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (ossIds.isEmpty()) {
            return;
        }
        Map<String, RemoteFile> ossMap = new HashMap<>();
        try {
            List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", ossIds));
            if (files != null) {
                files.forEach(f -> ossMap.put(String.valueOf(f.getOssId()), f));
            }
        } catch (Exception e) {
            log.warn("批量获取背景OSS文件信息失败: {}", e.getMessage());
        }
        for (DhBackground bg : backgrounds) {
            if (StringUtils.isNotBlank(bg.getOssId())) {
                RemoteFile file = ossMap.get(bg.getOssId());
                if (file != null && StringUtils.isNotBlank(file.getUrl())) {
                    bg.setPreviewUrl(file.getUrl());
                }
            }
        }
    }
}
