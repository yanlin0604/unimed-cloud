package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.dhcore.domain.DhAvatar;
import org.dromara.dhcore.domain.vo.DhAvatarVo;
import org.dromara.dhcore.mapper.DhAvatarMapper;
import org.dromara.dhcore.service.IDhAvatarService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<DhAvatarVo> listAvailableAvatars(Long userId) {
        LambdaQueryWrapper<DhAvatar> lqw = Wrappers.lambdaQuery();
        // 系统预设 + 用户自定义
        lqw.and(w -> w.eq(DhAvatar::getIsSystem, 1).or().eq(DhAvatar::getUserId, userId));
        lqw.eq(DhAvatar::getStatus, "0");
        lqw.orderByDesc(DhAvatar::getIsSystem).orderByDesc(DhAvatar::getCreateTime);

        List<DhAvatar> avatars = dhAvatarMapper.selectList(lqw);
        List<DhAvatarVo> result = new ArrayList<>();
        for (DhAvatar avatar : avatars) {
            result.add(DhConvertUtils.toAvatarVo(avatar));
        }
        return result;
    }

    @Override
    public DhAvatarVo saveAvatar(Long userId, String ossId, String name, String imageUrl) {
        DhAvatar avatar = new DhAvatar();
        avatar.setUserId(userId);
        avatar.setName(name != null ? name : "形象-" + DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss"));
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
