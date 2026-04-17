package org.dromara.dhcore.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dhcore.domain.vo.DhAvatarVo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.domain.vo.portal.PortalTemplateVo;
import org.dromara.dhcore.service.IDhAvatarService;
import org.dromara.dhcore.service.IDhMaterialService;
import org.dromara.dhcore.service.IDhVoiceService;
import org.dromara.dhcore.service.IPortalCreationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * C端创作资产服务实现类
 * 整合形象、音色、素材、模板的管理功能
 *
 * @author dhcore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortalCreationServiceImpl implements IPortalCreationService {

    private final IDhAvatarService avatarService;
    private final IDhVoiceService voiceService;
    private final IDhMaterialService materialService;

    // ==================== 形象管理 ====================

    @Override
    public List<DhAvatarVo> listAvatars(Long userId) {
        return avatarService.listAvailableAvatars(userId);
    }

    @Override
    public DhAvatarVo saveAvatar(Long userId, String ossId, String name, String imageUrl) {
        return avatarService.saveAvatar(userId, ossId, name, imageUrl);
    }

    @Override
    public void deleteAvatar(Long userId, Long avatarId) {
        avatarService.deleteAvatar(userId, avatarId);
    }

    // ==================== 音色管理 ====================

    @Override
    public List<DhVoiceVo> listVoices(Long userId) {
        return voiceService.listAvailableVoices(userId);
    }

    @Override
    public DhVoiceVo saveVoice(Long userId, String ossId, String name, String sampleUrl) {
        return voiceService.saveVoice(userId, ossId, name, sampleUrl);
    }

    @Override
    public void deleteVoice(Long userId, Long voiceId) {
        voiceService.deleteVoice(userId, voiceId);
    }

    // ==================== 素材管理 ====================

    @Override
    public List<DhMaterialVo> listMaterials(Long userId, String materialType) {
        return materialService.listByUserIdAndType(userId, materialType);
    }

    @Override
    public DhMaterialVo saveMaterial(Long userId, String ossId, String materialType, String fileName, String fileUrl) {
        return materialService.saveMaterial(userId, ossId, materialType, fileName, fileUrl);
    }

    @Override
    public void deleteMaterial(Long userId, Long materialId) {
        materialService.deleteMaterial(userId, materialId);
    }

    // ==================== 模板管理 ====================

    @Override
    public List<PortalTemplateVo> listTemplates() {
        // TODO: 待模板表完善后实现
        return new ArrayList<>();
    }
}
