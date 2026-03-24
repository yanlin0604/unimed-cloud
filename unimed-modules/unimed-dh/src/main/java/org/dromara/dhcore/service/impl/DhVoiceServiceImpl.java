package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
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
import org.dromara.dhcore.domain.DhVoice;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhVoiceBo;
import org.dromara.dhcore.domain.bo.DhVoiceQueryBo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.mapper.DhVoiceMapper;
import org.dromara.dhcore.service.IDhVoiceService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音色服务实现类
 *
 * @author dhcore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhVoiceServiceImpl implements IDhVoiceService {

    private final DhVoiceMapper dhVoiceMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @Override
    public List<DhVoiceVo> listAvailableVoices(Long userId) {
        LambdaQueryWrapper<DhVoice> lqw = Wrappers.lambdaQuery();
        lqw.and(w -> w.eq(DhVoice::getIsSystem, 1).or().eq(DhVoice::getUserId, userId));
        lqw.eq(DhVoice::getStatus, "0");
        lqw.orderByDesc(DhVoice::getIsSystem).orderByDesc(DhVoice::getCreateTime);

        List<DhVoice> voices = dhVoiceMapper.selectList(lqw);
        
        // 批量获取所有 ossId 对应的 OSS 信息
        List<String> ossIds = voices.stream()
            .map(DhVoice::getOssId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        
        Map<String, RemoteFile> ossMap = new HashMap<>();
        if (!ossIds.isEmpty()) {
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", ossIds));
                if (files != null) {
                    for (RemoteFile file : files) {
                        ossMap.put(String.valueOf(file.getOssId()), file);
                    }
                }
            } catch (Exception e) {
                log.warn("批量获取音色OSS信息失败", e);
            }
        }
        
        // 转换并填充临时 URL 和原文件名
        List<DhVoiceVo> result = new ArrayList<>();
        for (DhVoice voice : voices) {
            DhVoiceVo vo = DhConvertUtils.toVoiceVo(voice);
            
            // 根据 ossId 获取临时 URL 和原文件名
            if (StringUtils.isNotBlank(voice.getOssId())) {
                RemoteFile file = ossMap.get(voice.getOssId());
                if (file != null) {
                    vo.setSampleUrl(file.getUrl());
                    vo.setFileName(file.getOriginalName());
                }
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public DhVoiceVo saveVoice(Long userId, String ossId, String name, String sampleUrl) {
        DhVoice voice = new DhVoice();
        voice.setUserId(userId);
        
        // 如果用户未指定名称，从 OSS 获取原文件名
        String finalName = name;
        if (StringUtils.isBlank(finalName) && StringUtils.isNotBlank(ossId)) {
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(ossId);
                if (files != null && !files.isEmpty()) {
                    finalName = files.get(0).getOriginalName();
                }
            } catch (Exception e) {
                log.warn("获取音色原文件名失败: {}", ossId, e);
            }
        }
        // 如果仍未获取到名称，使用默认名称
        if (StringUtils.isBlank(finalName)) {
            finalName = "音色-" + DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss");
        }
        
        voice.setName(finalName);
        voice.setOssId(ossId);
        voice.setSampleUrl(sampleUrl);
        voice.setSource("upload");
        voice.setIsSystem(0);
        voice.setStatus("0");
        dhVoiceMapper.insert(voice);
        return DhConvertUtils.toVoiceVo(voice);
    }

    @Override
    public void deleteVoice(Long userId, Long voiceId) {
        DhVoice voice = dhVoiceMapper.selectById(voiceId);
        if (voice == null) {
            throw new ServiceException("音色不存在");
        }
        if (!userId.equals(voice.getUserId())) {
            throw new ServiceException("无权删除该音色");
        }
        if (voice.getIsSystem() == 1) {
            throw new ServiceException("系统预设音色不可删除");
        }
        dhVoiceMapper.deleteById(voiceId);
    }

    @Override
    public DhVoice getById(Long voiceId) {
        return dhVoiceMapper.selectById(voiceId);
    }

    @Override
    public DhVoice save(DhVoice voice) {
        dhVoiceMapper.insert(voice);
        return voice;
    }

    // ==================== B端管理方法 ====================

    @Override
    public TableDataInfo<DhVoiceVo> querySystemVoicePage(DhVoiceQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhVoice> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhVoice::getIsSystem, 1);
        lqw.like(StringUtils.isNotBlank(bo.getName()), DhVoice::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhVoice::getStatus, bo.getStatus());
        lqw.orderByAsc(DhVoice::getSortOrder).orderByDesc(DhVoice::getCreateTime);
        Page<DhVoice> page = dhVoiceMapper.selectPage(pageQuery.build(), lqw);
        List<DhVoiceVo> voList = page.getRecords().stream().map(DhConvertUtils::toVoiceVo).toList();
        return new TableDataInfo<>(voList, page.getTotal());
    }

    @Override
    public DhVoiceVo saveSystemVoice(DhVoiceBo bo) {
        DhVoice voice = new DhVoice();
        voice.setName(bo.getName());
        voice.setOssId(bo.getOssId());
        voice.setSampleUrl(bo.getSampleUrl());
        voice.setSource("system");
        voice.setIsSystem(1);
        voice.setStatus("0");
        voice.setSortOrder(bo.getSortOrder() != null ? bo.getSortOrder() : 0);
        voice.setRemark(bo.getRemark());
        dhVoiceMapper.insert(voice);
        return DhConvertUtils.toVoiceVo(voice);
    }

    @Override
    public DhVoiceVo updateSystemVoice(DhVoiceBo bo) {
        if (bo.getVoiceId() == null) {
            throw new ServiceException("音色ID不能为空");
        }
        DhVoice voice = dhVoiceMapper.selectById(bo.getVoiceId());
        if (voice == null) {
            throw new ServiceException("音色不存在");
        }
        if (voice.getIsSystem() != 1) {
            throw new ServiceException("该音色不是系统预设音色");
        }
        LambdaUpdateWrapper<DhVoice> uw = Wrappers.lambdaUpdate();
        uw.eq(DhVoice::getVoiceId, bo.getVoiceId());
        uw.set(StringUtils.isNotBlank(bo.getName()), DhVoice::getName, bo.getName());
        uw.set(bo.getSortOrder() != null, DhVoice::getSortOrder, bo.getSortOrder());
        uw.set(StringUtils.isNotBlank(bo.getRemark()), DhVoice::getRemark, bo.getRemark());
        dhVoiceMapper.update(null, uw);
        return DhConvertUtils.toVoiceVo(dhVoiceMapper.selectById(bo.getVoiceId()));
    }

    @Override
    public Boolean deleteSystemVoice(Long voiceId) {
        DhVoice voice = dhVoiceMapper.selectById(voiceId);
        if (voice == null) {
            throw new ServiceException("音色不存在");
        }
        if (voice.getIsSystem() != 1) {
            throw new ServiceException("该记录不是系统预设音色，无法从此接口删除");
        }
        return dhVoiceMapper.deleteById(voiceId) > 0;
    }

    @Override
    public DhVoiceVo changeSystemVoiceStatus(DhConfigStatusBo bo) {
        DhVoice voice = dhVoiceMapper.selectById(bo.getId());
        if (voice == null) {
            throw new ServiceException("音色不存在");
        }
        LambdaUpdateWrapper<DhVoice> uw = Wrappers.lambdaUpdate();
        uw.eq(DhVoice::getVoiceId, bo.getId());
        uw.set(DhVoice::getStatus, bo.getStatus());
        dhVoiceMapper.update(null, uw);
        voice.setStatus(bo.getStatus());
        return DhConvertUtils.toVoiceVo(voice);
    }
}
