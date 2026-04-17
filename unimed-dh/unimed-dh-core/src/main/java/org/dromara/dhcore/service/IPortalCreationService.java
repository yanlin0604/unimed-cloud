package org.dromara.dhcore.service;

import org.dromara.dhcore.domain.vo.DhAvatarVo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.domain.vo.portal.PortalTemplateVo;

import java.util.List;

/**
 * C端创作资产服务接口
 * 整合形象、音色、素材、模板的管理功能
 *
 * @author dhcore
 */
public interface IPortalCreationService {

    // ==================== 形象管理 ====================

    /**
     * 获取可用数字人形象列表
     *
     * @param userId 用户ID
     * @return 形象列表（系统预设 + 用户自定义）
     */
    List<DhAvatarVo> listAvatars(Long userId);

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

    // ==================== 音色管理 ====================

    /**
     * 获取可用音色列表
     *
     * @param userId 用户ID
     * @return 音色列表（系统预设 + 用户自定义）
     */
    List<DhVoiceVo> listVoices(Long userId);

    /**
     * 保存用户上传的音色
     *
     * @param userId    用户ID
     * @param ossId     OSS文件ID
     * @param name      音色名称（可选）
     * @param sampleUrl 音频URL（可选）
     * @return 保存后的音色
     */
    DhVoiceVo saveVoice(Long userId, String ossId, String name, String sampleUrl);

    /**
     * 删除用户自定义音色
     *
     * @param userId  用户ID
     * @param voiceId 音色ID
     */
    void deleteVoice(Long userId, Long voiceId);

    // ==================== 素材管理 ====================

    /**
     * 获取用户素材列表（含系统预设）
     *
     * @param userId       用户ID
     * @param materialType 素材类型过滤（VIDEO/IMAGE/AUDIO），传 null 时返回全部
     * @return 素材列表
     */
    List<DhMaterialVo> listMaterials(Long userId, String materialType);

    /**
     * 保存用户上传的素材
     *
     * @param userId       用户ID
     * @param ossId        OSS文件ID
     * @param materialType 素材类型
     * @param fileName     文件名（可选）
     * @param fileUrl      文件URL（可选）
     * @return 保存后的素材
     */
    DhMaterialVo saveMaterial(Long userId, String ossId, String materialType, String fileName, String fileUrl);

    /**
     * 删除素材
     *
     * @param userId     用户ID
     * @param materialId 素材ID
     */
    void deleteMaterial(Long userId, Long materialId);

    // ==================== 模板管理 ====================

    /**
     * 获取公开模板列表
     *
     * @return 模板列表
     */
    List<PortalTemplateVo> listTemplates();
}
