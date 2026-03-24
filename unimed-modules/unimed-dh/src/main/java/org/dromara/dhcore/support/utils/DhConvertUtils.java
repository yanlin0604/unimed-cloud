package org.dromara.dhcore.support.utils;

import org.dromara.dhcore.domain.DhAvatar;
import org.dromara.dhcore.domain.DhBackground;
import org.dromara.dhcore.domain.DhMaterial;
import org.dromara.dhcore.domain.DhVoice;
import org.dromara.dhcore.domain.vo.DhAvatarVo;
import org.dromara.dhcore.domain.vo.DhBackgroundVo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.domain.vo.portal.PortalBackgroundVo;

/**
 * 实体转换工具类
 * 
 * @author dhcore
 */
public final class DhConvertUtils {

    private DhConvertUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * DhAvatar 转 DhAvatarVo
     *
     * @param avatar 形象实体
     * @return 形象视图对象，null 返回 null
     */
    public static DhAvatarVo toAvatarVo(DhAvatar avatar) {
        if (avatar == null) {
            return null;
        }
        DhAvatarVo vo = new DhAvatarVo();
        vo.setAvatarId(avatar.getAvatarId());
        vo.setName(avatar.getName());
        vo.setImageUrl(avatar.getImageUrl());
        vo.setOssId(avatar.getOssId());
        vo.setIsSystem(avatar.getIsSystem() != null && avatar.getIsSystem() == 1);
        vo.setCreateTime(avatar.getCreateTime());
        return vo;
    }

    /**
     * DhVoice 转 DhVoiceVo
     *
     * @param voice 音色实体
     * @return 音色视图对象，null 返回 null
     */
    public static DhVoiceVo toVoiceVo(DhVoice voice) {
        if (voice == null) {
            return null;
        }
        DhVoiceVo vo = new DhVoiceVo();
        vo.setVoiceId(voice.getVoiceId());
        vo.setName(voice.getName());
        vo.setSampleUrl(voice.getSampleUrl());
        vo.setOssId(voice.getOssId());
        vo.setSource(voice.getSource());
        vo.setIsSystem(voice.getIsSystem() != null && voice.getIsSystem() == 1);
        vo.setCreateTime(voice.getCreateTime());
        return vo;
    }

    /**
     * DhMaterial 转 DhMaterialVo
     *
     * @param material 素材实体
     * @return 素材视图对象，null 返回 null
     */
    public static DhMaterialVo toMaterialVo(DhMaterial material) {
        if (material == null) {
            return null;
        }
        DhMaterialVo vo = new DhMaterialVo();
        vo.setMaterialId(material.getMaterialId());
        vo.setMaterialType(material.getMaterialType());
        vo.setName(material.getName());
        vo.setFileName(material.getFileName());
        vo.setFileUrl(material.getFileUrl());
        vo.setThumbnailUrl(material.getThumbnailUrl());
        vo.setFileSize(material.getFileSize());
        vo.setDuration(material.getDuration());
        vo.setIsSystem(material.getIsSystem() != null && material.getIsSystem() == 1);
        vo.setStatus(material.getStatus());
        vo.setSortOrder(material.getSortOrder());
        vo.setRemark(material.getRemark());
        vo.setCreateTime(material.getCreateTime());
        return vo;
    }

    /**
     * DhBackground 转 DhBackgroundVo（B端）
     *
     * @param background 背景实体
     * @return 背景视图对象，null 返回 null
     */
    public static DhBackgroundVo toBackgroundVo(DhBackground background) {
        if (background == null) {
            return null;
        }
        DhBackgroundVo vo = new DhBackgroundVo();
        vo.setBackgroundId(background.getBackgroundId());
        vo.setName(background.getName());
        vo.setBgType(background.getBgType());
        vo.setOssId(background.getOssId());
        vo.setPreviewUrl(background.getPreviewUrl());
        vo.setIsSystem(background.getIsSystem() != null && background.getIsSystem() == 1);
        vo.setStatus(background.getStatus());
        vo.setSortOrder(background.getSortOrder());
        vo.setRemark(background.getRemark());
        vo.setCreateTime(background.getCreateTime());
        return vo;
    }

    /**
     * DhBackground 转 PortalBackgroundVo（C端）
     *
     * @param background 背景实体
     * @return C端背景视图对象，null 返回 null
     */
    public static PortalBackgroundVo toPortalBackgroundVo(DhBackground background) {
        if (background == null) {
            return null;
        }
        PortalBackgroundVo vo = new PortalBackgroundVo();
        vo.setBackgroundId(background.getBackgroundId());
        vo.setName(background.getName());
        vo.setBgType(background.getBgType());
        vo.setPreviewUrl(background.getPreviewUrl());
        vo.setSortOrder(background.getSortOrder());
        return vo;
    }
}
