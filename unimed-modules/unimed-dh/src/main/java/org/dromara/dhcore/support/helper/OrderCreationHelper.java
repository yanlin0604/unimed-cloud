package org.dromara.dhcore.support.helper;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.dhcore.domain.*;
import org.dromara.dhcore.domain.bo.portal.PortalOrderCreateBo;
import org.dromara.dhcore.mapper.DhAvatarMapper;
import org.dromara.dhcore.mapper.DhMaterialMapper;
import org.dromara.dhcore.mapper.DhOrderMaterialMapper;
import org.dromara.dhcore.mapper.DhOrderProcessLogMapper;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.mapper.DhVoiceMapper;
import org.dromara.dhcore.mapper.DhWalletLogMapper;
import org.dromara.dhcore.service.IDhConfigService;
import org.dromara.dhcore.support.enums.DhMaterialType;
import org.dromara.dhcore.support.enums.DhOrderStatus;
import org.dromara.dhcore.domain.bo.DhMemberConfigQueryBo;
import org.dromara.dhcore.domain.vo.DhMemberConfigVo;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单创建辅助类
 * 拆分订单创建的复杂逻辑
 *
 * @author dhcore
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationHelper {

    private final IDhConfigService configService;
    private final DhAvatarMapper avatarMapper;
    private final DhVoiceMapper voiceMapper;
    private final DhMaterialMapper materialMapper;
    private final DhOrderMaterialMapper orderMaterialMapper;
    private final DhUserProfileMapper userProfileMapper;
    private final DhWalletLogMapper walletLogMapper;
    private final DhOrderProcessLogMapper processLogMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    /**
     * 查询会员等级定价
     */
    public BigDecimal resolveMemberPrice(String memberLevel) {
        DhMemberConfigQueryBo queryBo = new DhMemberConfigQueryBo();
        queryBo.setLevel(memberLevel);
        queryBo.setStatus("0"); // 启用状态
        org.dromara.common.mybatis.core.page.PageQuery pageQuery = new org.dromara.common.mybatis.core.page.PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(1);
        var result = configService.queryMemberConfigPage(queryBo, pageQuery);
        DhMemberConfigVo config = (result != null && result.getRows() != null && !result.getRows().isEmpty())
            ? result.getRows().get(0) : null;
        return config != null && config.getOrderPrice() != null
            ? config.getOrderPrice()
            : BigDecimal.ZERO;
    }

    /**
     * 解析最终形象ID
     * 如果有临时上传的形象，创建临时记录并返回新ID
     */
    public Long resolveAvatarId(Long userId, Long avatarId, String avatarUploadOssId) {
        if (StringUtils.isNotBlank(avatarUploadOssId)) {
            DhAvatar tempAvatar = new DhAvatar();
            tempAvatar.setUserId(userId);
            
            // 从 OSS 获取原文件名
            String fileName = null;
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(avatarUploadOssId);
                if (files != null && !files.isEmpty()) {
                    fileName = files.get(0).getOriginalName();
                }
            } catch (Exception e) {
                log.warn("获取临时形象原文件名失败: {}", avatarUploadOssId, e);
            }
            if (StringUtils.isBlank(fileName)) {
                fileName = "形象-" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            }
            
            tempAvatar.setName(fileName);
            tempAvatar.setOssId(avatarUploadOssId);
            tempAvatar.setIsSystem(0);
            tempAvatar.setStatus("0");
            avatarMapper.insert(tempAvatar);
            return tempAvatar.getAvatarId();
        }
        return avatarId;
    }

    /**
     * 解析最终音色ID
     * 如果有临时上传的音色，创建临时记录并返回新ID
     */
    public Long resolveVoiceId(Long userId, Long voiceId, String voiceUploadOssId) {
        if (StringUtils.isNotBlank(voiceUploadOssId)) {
            DhVoice tempVoice = new DhVoice();
            tempVoice.setUserId(userId);
            
            // 从 OSS 获取原文件名
            String fileName = null;
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(voiceUploadOssId);
                if (files != null && !files.isEmpty()) {
                    fileName = files.get(0).getOriginalName();
                }
            } catch (Exception e) {
                log.warn("获取临时音色原文件名失败: {}", voiceUploadOssId, e);
            }
            if (StringUtils.isBlank(fileName)) {
                fileName = "音色-" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            }
            
            tempVoice.setName(fileName);
            tempVoice.setOssId(voiceUploadOssId);
            tempVoice.setSource("upload");
            tempVoice.setIsSystem(0);
            tempVoice.setStatus("0");
            voiceMapper.insert(tempVoice);
            return tempVoice.getVoiceId();
        }
        return voiceId;
    }

    /**
     * 构建订单实体
     */
    public DhOrder buildOrder(Long userId, String userName, DhUserProfile profile,
            PortalOrderCreateBo bo, BigDecimal orderPrice) {
        Date now = new Date();
        DhOrder order = new DhOrder();
        order.setOrderNo(generateOrderNo());
        order.setTitle(bo.getTitle());
        order.setApplicantName(userName);
        order.setMemberLevel(profile.getMemberLevel());
        order.setStatus(DhOrderStatus.PENDING);
        order.setIsRedo(0);
        order.setPriority(0);
        order.setScriptText(bo.getScriptText());
        order.setToneStyle(bo.getToneStyle());
        order.setSceneType(bo.getSceneType());
        order.setSpeechSpeed(bo.getSpeechSpeed());
        order.setVideoRatio(bo.getVideoRatio());
        order.setVideoResolution(bo.getVideoResolution());
        order.setVideoDuration(bo.getVideoDuration());
        order.setContactInfo(bo.getContactInfo());
        order.setCopyrightDeclared(bo.getCopyrightDeclared());
        order.setOrderAmount(orderPrice);
        order.setActualAmount(orderPrice);
        order.setDiscountRate(BigDecimal.ONE);
        return order;
    }

    /**
     * 关联素材
     *
     * @param orderId              订单ID
     * @param materialIds          普通素材ID列表
     * @param avatarId             已保存的形象ID
     * @param avatarUploadOssId    形象上传OSS ID（优先于 avatarId）
     * @param voiceId              已保存的音色ID
     * @param voiceUploadOssId     音色上传OSS ID（优先于 voiceId）
     * @param referenceVideoOssId  参考视频OSS ID
     */
    public void attachMaterials(Long orderId, List<Long> materialIds,
            Long avatarId, String avatarUploadOssId,
            Long voiceId, String voiceUploadOssId,
            String referenceVideoOssId) {
        int sort = 0;
        
        // 关联普通素材
        if (materialIds != null && !materialIds.isEmpty()) {
            for (Long materialId : materialIds) {
                DhMaterial material = materialMapper.selectById(materialId);
                if (material == null) {
                    log.warn("素材不存在: {}", materialId);
                    continue;
                }
                DhOrderMaterial om = new DhOrderMaterial();
                om.setOrderId(orderId);
                om.setFileId(String.valueOf(materialId));
                om.setFileName(material.getFileName() != null ? material.getFileName() : material.getName());
                om.setFileUrl(material.getFileUrl());
                om.setFileType(material.getMaterialType());
                om.setThumbnailUrl(material.getThumbnailUrl());
                om.setSort(sort++);
                orderMaterialMapper.insert(om);
            }
        }

        // 关联已保存的形象
        if (avatarId != null && StringUtils.isBlank(avatarUploadOssId)) {
            DhAvatar avatar = avatarMapper.selectById(avatarId);
            if (avatar != null) {
                DhOrderMaterial avatarMaterial = new DhOrderMaterial();
                avatarMaterial.setOrderId(orderId);
                avatarMaterial.setFileId(avatar.getOssId());
                avatarMaterial.setFileName(avatar.getName());
                avatarMaterial.setFileType(DhMaterialType.AVATAR.getValue());
                avatarMaterial.setSort(sort++);
                // 获取 OSS 文件 URL
                if (StringUtils.isNotBlank(avatar.getOssId())) {
                    try {
                        List<RemoteFile> files = remoteFileService.selectByIds(avatar.getOssId());
                        if (files != null && !files.isEmpty()) {
                            avatarMaterial.setFileUrl(files.get(0).getUrl());
                        }
                    } catch (Exception e) {
                        log.warn("获取已保存形象OSS信息失败: {}", avatar.getOssId(), e);
                    }
                }
                orderMaterialMapper.insert(avatarMaterial);
                log.info("订单 {} 关联已保存形象: avatarId={}", orderId, avatarId);
            }
        }

        // 关联临时上传的形象（存入 dh_order_material，fileType=AVATAR）
        if (StringUtils.isNotBlank(avatarUploadOssId)) {
            DhOrderMaterial avatarMaterial = new DhOrderMaterial();
            avatarMaterial.setOrderId(orderId);
            avatarMaterial.setFileId(avatarUploadOssId);
            avatarMaterial.setFileType(DhMaterialType.AVATAR.getValue());
            avatarMaterial.setSort(sort++);
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(avatarUploadOssId);
                if (files != null && !files.isEmpty()) {
                    RemoteFile file = files.get(0);
                    avatarMaterial.setFileName(file.getOriginalName() != null ? file.getOriginalName() : file.getName());
                    avatarMaterial.setFileUrl(file.getUrl());
                }
            } catch (Exception e) {
                log.warn("获取形象OSS信息失败: {}", avatarUploadOssId, e);
            }
            if (StringUtils.isBlank(avatarMaterial.getFileName())) {
                avatarMaterial.setFileName("avatar_" + avatarUploadOssId);
            }
            if (StringUtils.isBlank(avatarMaterial.getFileUrl())) {
                avatarMaterial.setFileUrl("");
            }
            orderMaterialMapper.insert(avatarMaterial);
            log.info("订单 {} 关联形象素材: ossId={}", orderId, avatarUploadOssId);
        }

        // 关联已保存的音色
        if (voiceId != null && StringUtils.isBlank(voiceUploadOssId)) {
            DhVoice voice = voiceMapper.selectById(voiceId);
            if (voice != null) {
                DhOrderMaterial voiceMaterial = new DhOrderMaterial();
                voiceMaterial.setOrderId(orderId);
                voiceMaterial.setFileId(voice.getOssId());
                voiceMaterial.setFileName(voice.getName());
                voiceMaterial.setFileType(DhMaterialType.VOICE.getValue());
                voiceMaterial.setSort(sort++);
                // 获取 OSS 文件 URL
                if (StringUtils.isNotBlank(voice.getOssId())) {
                    try {
                        List<RemoteFile> files = remoteFileService.selectByIds(voice.getOssId());
                        if (files != null && !files.isEmpty()) {
                            voiceMaterial.setFileUrl(files.get(0).getUrl());
                        }
                    } catch (Exception e) {
                        log.warn("获取已保存音色OSS信息失败: {}", voice.getOssId(), e);
                    }
                }
                orderMaterialMapper.insert(voiceMaterial);
                log.info("订单 {} 关联已保存音色: voiceId={}", orderId, voiceId);
            }
        }

        // 关联临时上传的音色（存入 dh_order_material，fileType=VOICE）
        if (StringUtils.isNotBlank(voiceUploadOssId)) {
            DhOrderMaterial voiceMaterial = new DhOrderMaterial();
            voiceMaterial.setOrderId(orderId);
            voiceMaterial.setFileId(voiceUploadOssId);
            voiceMaterial.setFileType(DhMaterialType.VOICE.getValue());
            voiceMaterial.setSort(sort++);
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(voiceUploadOssId);
                if (files != null && !files.isEmpty()) {
                    RemoteFile file = files.get(0);
                    voiceMaterial.setFileName(file.getOriginalName() != null ? file.getOriginalName() : file.getName());
                    voiceMaterial.setFileUrl(file.getUrl());
                }
            } catch (Exception e) {
                log.warn("获取音色OSS信息失败: {}", voiceUploadOssId, e);
            }
            if (StringUtils.isBlank(voiceMaterial.getFileName())) {
                voiceMaterial.setFileName("voice_" + voiceUploadOssId);
            }
            if (StringUtils.isBlank(voiceMaterial.getFileUrl())) {
                voiceMaterial.setFileUrl("");
            }
            orderMaterialMapper.insert(voiceMaterial);
            log.info("订单 {} 关联音色素材: ossId={}", orderId, voiceUploadOssId);
        }
        
        // 关联参考视频
        if (StringUtils.isNotBlank(referenceVideoOssId)) {
            DhOrderMaterial refVideo = new DhOrderMaterial();
            refVideo.setOrderId(orderId);
            refVideo.setFileId(referenceVideoOssId);
            refVideo.setFileType(DhMaterialType.REFERENCE_VIDEO.getValue());
            refVideo.setSort(sort++);
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(referenceVideoOssId);
                if (files != null && !files.isEmpty()) {
                    RemoteFile file = files.get(0);
                    refVideo.setFileName(file.getOriginalName() != null ? file.getOriginalName() : file.getName());
                    refVideo.setFileUrl(file.getUrl());
                }
            } catch (Exception e) {
                log.warn("获取参考视频OSS信息失败: {}", referenceVideoOssId, e);
            }
            if (StringUtils.isBlank(refVideo.getFileName())) {
                refVideo.setFileName("reference_video_" + referenceVideoOssId);
            }
            if (StringUtils.isBlank(refVideo.getFileUrl())) {
                refVideo.setFileUrl("");
            }
            orderMaterialMapper.insert(refVideo);
            log.info("订单 {} 关联参考视频: ossId={}", orderId, referenceVideoOssId);
        }
    }

    /**
     * 扣减余额
     */
    public void deductBalance(DhUserProfile profile, BigDecimal amount) {
        BigDecimal newBalance = (profile.getWalletBalance() != null ? profile.getWalletBalance() : BigDecimal.ZERO)
            .subtract(amount);
        profile.setWalletBalance(newBalance);
        profile.setTotalConsume(
            (profile.getTotalConsume() != null ? profile.getTotalConsume() : BigDecimal.ZERO).add(amount)
        );
        profile.setOrderCount((profile.getOrderCount() != null ? profile.getOrderCount() : 0) + 1);
        userProfileMapper.updateById(profile);
    }

    /**
     * 记录钱包流水（消费）
     */
    public void recordWalletLog(Long userId, String userName, BigDecimal amount, 
            BigDecimal balanceAfter, Long orderId, String orderNo) {
        recordWalletLog(userId, userName, amount, balanceAfter, orderId, orderNo, "CONSUME");
    }

    /**
     * 记录钱包流水
     */
    public void recordWalletLog(Long userId, String userName, BigDecimal amount, 
            BigDecimal balanceAfter, Long orderId, String orderNo, String type) {
        DhWalletLog walletLog = new DhWalletLog();
        walletLog.setUserId(userId);
        walletLog.setUserName(userName);
        walletLog.setType(type);
        walletLog.setAmount("CANCEL".equals(type) ? amount : amount.negate());
        walletLog.setBalanceAfter(balanceAfter);
        walletLog.setRelatedOrderId(orderId);
        walletLog.setOperatorName(userName);
        walletLog.setRemark("CANCEL".equals(type) ? "取消订单退款：" + orderNo : "创建订单：" + orderNo);
        walletLogMapper.insert(walletLog);
    }

    /**
     * 记录处理日志
     */
    public void recordProcessLog(Long orderId, String operatorName, String actionText) {
        DhOrderProcessLog processLog = new DhOrderProcessLog();
        processLog.setOrderId(orderId);
        processLog.setActionText(actionText);
        processLog.setOperatorName(operatorName);
        processLog.setOperateTime(new Date());
        processLogMapper.insert(processLog);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "DH" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
    }
}
