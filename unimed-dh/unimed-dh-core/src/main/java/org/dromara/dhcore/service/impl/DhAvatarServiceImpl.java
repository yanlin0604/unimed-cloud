package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.dhcore.domain.DhAvatar;
import org.dromara.dhcore.domain.vo.DhAvatarVo;
import org.dromara.dhcore.mapper.DhAvatarMapper;
import org.dromara.dhcore.service.IDhAvatarService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数字人形象服务实现类
 *
 * @author dhcore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhAvatarServiceImpl implements IDhAvatarService {

    private final DhAvatarMapper dhAvatarMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @Override
    public List<DhAvatarVo> listAvailableAvatars(Long userId) {
        LambdaQueryWrapper<DhAvatar> lqw = Wrappers.lambdaQuery();
        // 系统预设 + 用户自定义
        lqw.and(w -> w.eq(DhAvatar::getIsSystem, 1).or().eq(DhAvatar::getUserId, userId));
        lqw.eq(DhAvatar::getStatus, "0");
        lqw.orderByDesc(DhAvatar::getIsSystem).orderByDesc(DhAvatar::getCreateTime);

        List<DhAvatar> avatars = dhAvatarMapper.selectList(lqw);
        
        // 批量获取所有 ossId 对应的 OSS 信息
        List<String> ossIds = avatars.stream()
            .map(DhAvatar::getOssId)
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
                log.warn("批量获取形象OSS信息失败", e);
            }
        }
        
        // 转换并填充临时 URL 和原文件名
        List<DhAvatarVo> result = new ArrayList<>();
        for (DhAvatar avatar : avatars) {
            DhAvatarVo vo = DhConvertUtils.toAvatarVo(avatar);
            
            // 根据 ossId 获取临时 URL 和原文件名
            if (StringUtils.isNotBlank(avatar.getOssId())) {
                RemoteFile file = ossMap.get(avatar.getOssId());
                if (file != null) {
                    vo.setImageUrl(file.getUrl());
                    vo.setFileName(file.getOriginalName());
                }
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public DhAvatarVo saveAvatar(Long userId, String ossId, String name, String imageUrl) {
        DhAvatar avatar = new DhAvatar();
        avatar.setUserId(userId);
        
        // 如果用户未指定名称，从 OSS 获取原文件名
        String finalName = name;
        if (StringUtils.isBlank(finalName) && StringUtils.isNotBlank(ossId)) {
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(ossId);
                if (files != null && !files.isEmpty()) {
                    finalName = files.get(0).getOriginalName();
                }
            } catch (Exception e) {
                log.warn("获取形象原文件名失败: {}", ossId, e);
            }
        }
        // 如果仍未获取到名称，使用默认名称
        if (StringUtils.isBlank(finalName)) {
            finalName = "形象-" + DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss");
        }
        
        avatar.setName(finalName);
        avatar.setOssId(ossId);
        avatar.setImageUrl(imageUrl);
        avatar.setIsSystem(0);
        avatar.setStatus("0");
        dhAvatarMapper.insert(avatar);
        return DhConvertUtils.toAvatarVo(avatar);
    }

    @Override
    public void deleteAvatar(Long userId, Long avatarId) {
        DhAvatar avatar = dhAvatarMapper.selectById(avatarId);
        if (avatar == null) {
            throw new ServiceException("形象不存在");
        }
        if (!userId.equals(avatar.getUserId())) {
            throw new ServiceException("无权删除该形象");
        }
        if (avatar.getIsSystem() == 1) {
            throw new ServiceException("系统预设形象不可删除");
        }
        dhAvatarMapper.deleteById(avatarId);
    }

    @Override
    public DhAvatar getById(Long avatarId) {
        return dhAvatarMapper.selectById(avatarId);
    }

    @Override
    public DhAvatar save(DhAvatar avatar) {
        dhAvatarMapper.insert(avatar);
        return avatar;
    }
}
