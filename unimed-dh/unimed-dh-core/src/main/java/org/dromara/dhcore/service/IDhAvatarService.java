package org.dromara.dhcore.service;

import org.dromara.dhcore.domain.DhAvatar;
import org.dromara.dhcore.domain.vo.DhAvatarVo;

import java.util.List;

/**
 * 数字人形象服务接口
 *
 * @author dhcore
 */
public interface IDhAvatarService {

    /**
     * 获取用户可用的形象列表
     * 包括系统预设形象和用户自定义形象
     *
     * @param userId 用户ID
     * @return 形象列表
     */
    List<DhAvatarVo> listAvailableAvatars(Long userId);

    /**
     * 保存用户上传的形象
     *
     * @param userId   用户ID
     * @param ossId    OSS文件ID
     * @param name     形象名称（可选）
     * @param imageUrl 图片URL（可选）
     * @return 保存后的形象
     */
    DhAvatarVo saveAvatar(Long userId, String ossId, String name, String imageUrl);

    /**
     * 删除用户自定义形象
     *
     * @param userId   用户ID
     * @param avatarId 形象ID
     */
    void deleteAvatar(Long userId, Long avatarId);

    /**
     * 根据ID获取形象
     *
     * @param avatarId 形象ID
     * @return 形象实体
     */
    DhAvatar getById(Long avatarId);

    /**
     * 保存形象（内部方法，用于临时上传资产处理）
     *
     * @param avatar 形象实体
     * @return 保存后的形象
     */
    DhAvatar save(DhAvatar avatar);
}
