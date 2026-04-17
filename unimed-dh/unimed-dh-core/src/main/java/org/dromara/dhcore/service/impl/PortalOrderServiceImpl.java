package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.*;
import org.dromara.dhcore.domain.bo.portal.PortalOrderCreateBo;
import org.dromara.dhcore.domain.vo.DhProcessLogVo;
import org.dromara.dhcore.domain.vo.DhProductionAssetVo;
import org.dromara.dhcore.domain.vo.DhOrderDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderDownloadVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderVo;
import org.dromara.dhcore.mapper.*;
import org.dromara.dhcore.service.IDhOrderService;
import org.dromara.dhcore.service.IPortalOrderService;
import org.dromara.dhcore.support.enums.DhMaterialType;
import org.dromara.dhcore.support.enums.DhOrderStatus;
import org.dromara.dhcore.support.helper.OrderCreationHelper;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * C端订单服务实现类
 *
 * @author dhcore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortalOrderServiceImpl implements IPortalOrderService {

    private final IDhOrderService dhOrderService;
    private final DhOrderMapper orderMapper;
    private final DhUserProfileMapper userProfileMapper;
    private final DhOrderMaterialMapper orderMaterialMapper;
    private final DhOrderProductionAssetMapper productionAssetMapper;
    private final OrderCreationHelper orderCreationHelper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @Override
    public TableDataInfo<PortalOrderVo> listOrders(Long userId, String status, String keyword, PageQuery pageQuery) {
        LambdaQueryWrapper<DhOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhOrder::getCreateBy, userId);
        lqw.eq(StringUtils.isNotBlank(status), DhOrder::getStatus, status);
        if (StringUtils.isNotBlank(keyword)) {
            lqw.and(w -> w.like(DhOrder::getOrderNo, keyword).or().like(DhOrder::getTitle, keyword));
        }
        lqw.orderByDesc(DhOrder::getCreateTime);

        Page<DhOrder> page = orderMapper.selectPage(pageQuery.build(), lqw);
        List<PortalOrderVo> rows = page.getRecords().stream().map(this::toPortalOrderVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    public PortalOrderDetailVo getOrderDetail(Long userId, Long orderId) {
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在: " + orderId);
        }
        if (!userId.equals(order.getCreateBy())) {
            throw new ServiceException("无权查看该订单");
        }
        DhOrderDetailVo detailVo = dhOrderService.queryOrderDetail(orderId);
        DhUserProfile userProfile = userProfileMapper.selectById(userId);
        return toPortalOrderDetailVo(order, detailVo, orderId, userProfile);
    }

    @Override
    public Map<String, Long> getOrderStats(Long userId) {
        Map<String, Long> statsMap = new LinkedHashMap<>();
        statsMap.put("total", countByUserAndStatus(userId, null));
        statsMap.put("pending", countByUserAndStatus(userId, DhOrderStatus.PENDING));
        statsMap.put("processing", countByUserAndStatus(userId, DhOrderStatus.PROCESSING)
            + countByUserAndStatus(userId, DhOrderStatus.TO_UPLOAD)
            + countByUserAndStatus(userId, DhOrderStatus.REDO));
        statsMap.put("completed", countByUserAndStatus(userId, DhOrderStatus.COMPLETED));
        statsMap.put("cancelled", countByUserAndStatus(userId, DhOrderStatus.CANCELLED));
        statsMap.put("rejected", countByUserAndStatus(userId, DhOrderStatus.REJECTED));
        return statsMap;
    }

    @Override
    public Long countCompletedOrders(Long userId) {
        return countByUserAndStatus(userId, DhOrderStatus.COMPLETED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PortalOrderVo createOrder(Long userId, String userName, PortalOrderCreateBo bo) {
        // 1. 获取用户画像
        DhUserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) {
            throw new ServiceException("用户信息不存在");
        }

        // 2. 查询会员等级定价
        BigDecimal orderPrice = orderCreationHelper.resolveMemberPrice(profile.getMemberLevel());

        // 3. 校验余额
        BigDecimal balance = profile.getWalletBalance() != null ? profile.getWalletBalance() : BigDecimal.ZERO;
        if (balance.compareTo(orderPrice) < 0) {
            throw new ServiceException("余额不足，当前余额：" + balance + "，订单单价：" + orderPrice);
        }

        // 4. 处理临时上传资产（形象/音色），创建临时记录
        orderCreationHelper.resolveAvatarId(userId, bo.getAvatarId(), bo.getAvatarUploadOssId());
        orderCreationHelper.resolveVoiceId(userId, bo.getVoiceId(), bo.getVoiceUploadOssId());

        // 5. 创建订单
        DhOrder order = orderCreationHelper.buildOrder(userId, userName, profile, bo, orderPrice);
        orderMapper.insert(order);

        // 6. 关联素材（包括普通素材、形象、音色、参考视频）
        orderCreationHelper.attachMaterials(order.getOrderId(), bo.getMaterialIds(),
            bo.getAvatarId(), bo.getAvatarUploadOssId(),
            bo.getVoiceId(), bo.getVoiceUploadOssId(),
            bo.getReferenceVideoOssId());

        // 7. 扣减余额
        orderCreationHelper.deductBalance(profile, orderPrice);

        // 8. 记录钱包流水
        orderCreationHelper.recordWalletLog(userId, userName, orderPrice,
            profile.getWalletBalance(), order.getOrderId(), order.getOrderNo());

        // 9. 记录处理日志
        orderCreationHelper.recordProcessLog(order.getOrderId(), userName, "用户提交订单");

        return toPortalOrderVo(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, String userName, Long orderId, String reason) {
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在: " + orderId);
        }
        if (!userId.equals(order.getCreateBy())) {
            throw new ServiceException("无权操作该订单");
        }
        if (!DhOrderStatus.PENDING.equals(order.getStatus())) {
            throw new ServiceException("仅待处理状态的订单可取消");
        }

        // 更新订单状态
        order.setStatus(DhOrderStatus.CANCELLED);
        order.setCancelReason(StringUtils.isNotBlank(reason) ? reason : "用户主动取消");
        orderMapper.updateById(order);

        // 退还余额
        BigDecimal refundAmount = order.getActualAmount() != null ? order.getActualAmount() : BigDecimal.ZERO;
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            DhUserProfile profile = userProfileMapper.selectById(userId);
            if (profile != null) {
                BigDecimal newBalance = (profile.getWalletBalance() != null ? profile.getWalletBalance() : BigDecimal.ZERO)
                    .add(refundAmount);
                profile.setWalletBalance(newBalance);
                userProfileMapper.updateById(profile);

                // 记录钱包流水
                orderCreationHelper.recordWalletLog(userId, userName, refundAmount,
                    newBalance, orderId, order.getOrderNo(), "CANCEL");
            }
        }

        // 记录处理日志
        orderCreationHelper.recordProcessLog(orderId, userName, "用户取消订单");
    }

    // ==================== 私有方法 ====================

    private Long countByUserAndStatus(Long userId, DhOrderStatus status) {
        LambdaQueryWrapper<DhOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhOrder::getCreateBy, userId);
        if (status != null) {
            lqw.eq(DhOrder::getStatus, status);
        }
        return orderMapper.selectCount(lqw);
    }

    private PortalOrderVo toPortalOrderVo(DhOrder order) {
        PortalOrderVo vo = new PortalOrderVo();
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setStatus(order.getStatus() != null ? order.getStatus().getValue() : null);
        vo.setOrderAmount(order.getActualAmount());
        vo.setCreateTime(order.getCreateTime());
        vo.setExpectedDelivery(resolveExpectedDelivery(order));
        vo.setCoverUrl(resolveCoverUrl(order));
        return vo;
    }

    private String resolveCoverUrl(DhOrder order) {
        // 优先使用订单已记录的成片视频地址
        if (StringUtils.isNotBlank(order.getResultVideoUrl())) {
            return normalizePortalMediaUrl(order.getResultVideoUrl());
        }
        // 其次查询生产资产表
        try {
            LambdaQueryWrapper<DhOrderProductionAsset> assetQuery = Wrappers.lambdaQuery();
            assetQuery.eq(DhOrderProductionAsset::getOrderId, order.getOrderId());
            assetQuery.last("LIMIT 1");
            DhOrderProductionAsset asset = productionAssetMapper.selectOne(assetQuery);
            if (asset != null && StringUtils.isNotBlank(asset.getOutputVideoUrl())) {
                return normalizePortalMediaUrl(asset.getOutputVideoUrl());
            }
        } catch (Exception e) {
            log.warn("获取生产资产封面URL失败, orderId={}", order.getOrderId(), e);
        }
        // 最后使用第一条素材的缩略图
        try {
            LambdaQueryWrapper<DhOrderMaterial> materialQuery = Wrappers.lambdaQuery();
            materialQuery.eq(DhOrderMaterial::getOrderId, order.getOrderId());
            materialQuery.orderByAsc(DhOrderMaterial::getSort);
            materialQuery.last("LIMIT 1");
            DhOrderMaterial material = orderMaterialMapper.selectOne(materialQuery);
            if (material != null && StringUtils.isNotBlank(material.getThumbnailUrl())) {
                return material.getThumbnailUrl();
            }
        } catch (Exception e) {
            log.warn("获取素材缩略图封面URL失败, orderId={}", order.getOrderId(), e);
        }
        return null;
    }

    private PortalOrderDetailVo toPortalOrderDetailVo(DhOrder order, DhOrderDetailVo detailVo, Long orderId, DhUserProfile userProfile) {
        PortalOrderDetailVo vo = new PortalOrderDetailVo();
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setStatus(order.getStatus() != null ? order.getStatus().getValue() : null);
        vo.setIsRedo(order.getIsRedo());
        vo.setScriptText(order.getScriptText());
        vo.setOrderAmount(order.getOrderAmount());
        vo.setVideoRatio(order.getVideoRatio());
        vo.setVideoResolution(order.getVideoResolution());
        vo.setVideoDuration(order.getVideoDuration());
        vo.setActualAmount(order.getActualAmount());
        vo.setCreateTime(order.getCreateTime());
        vo.setExpectedDelivery(resolveExpectedDelivery(order));
        vo.setCompletedTime(order.getCompletedTime());
        vo.setAssigneeName(order.getAssigneeName());
        vo.setContactInfo(order.getContactInfo());
        if (userProfile != null) {
            vo.setApplicantName(userProfile.getUserName());
            vo.setApplicantPhone(userProfile.getPhone());
        }
        vo.setToneStyle(order.getToneStyle());
        vo.setSceneType(order.getSceneType());
        vo.setSpeechSpeed(order.getSpeechSpeed());
        vo.setCancelReason(order.getCancelReason());
        vo.setRejectReason(order.getRejectReason());
        vo.setRedoReason(order.getRedoReason());

        DhProductionAssetVo productionAsset = detailVo.getProductionAsset();
        String resultVideoUrl = detailVo.getResultVideoUrl();
        if (StringUtils.isBlank(resultVideoUrl) && productionAsset != null) {
            resultVideoUrl = productionAsset.getOutputVideoUrl();
        }
        vo.setResultVideoUrl(normalizePortalMediaUrl(resultVideoUrl));
        if (productionAsset != null) {
            vo.setResultVideoName(productionAsset.getOutputVideoName());
            vo.setResultVideoDuration(productionAsset.getOutputVideoDurationSec());
            vo.setResultVideoSizeMb(productionAsset.getOutputVideoSizeMb());
        }
        vo.setProgressNodes(buildProgressNodes(detailVo.getProcessLogs(), order.getStatus()));

        // 查询订单素材
        LambdaQueryWrapper<DhOrderMaterial> materialQuery = Wrappers.lambdaQuery();
        materialQuery.eq(DhOrderMaterial::getOrderId, orderId);
        materialQuery.orderByAsc(DhOrderMaterial::getSort);
        List<DhOrderMaterial> orderMaterials = orderMaterialMapper.selectList(materialQuery);

        if (orderMaterials != null && !orderMaterials.isEmpty()) {
            // 收集所有需要更新URL的fileId
            List<String> fileIds = orderMaterials.stream()
                .map(DhOrderMaterial::getFileId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();

            // 批量从OSS获取最新的URL
            Map<String, RemoteFile> fileMap = new HashMap<>();
            if (!fileIds.isEmpty()) {
                try {
                    List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", fileIds));
                    if (files != null) {
                        for (RemoteFile f : files) {
                            fileMap.put(String.valueOf(f.getOssId()), f);
                        }
                    }
                } catch (Exception e) {
                    log.warn("批量获取OSS文件URL失败", e);
                }
            }

            // 分类处理素材
            List<PortalOrderDetailVo.PortalOrderMaterialVo> materials = new ArrayList<>();
            for (DhOrderMaterial om : orderMaterials) {
                String fileType = om.getFileType();

                // 获取实时 URL
                String fileId = om.getFileId();
                RemoteFile file = fileMap.get(fileId);
                String fileUrl = normalizePortalMediaUrl(file != null ? file.getUrl() : om.getFileUrl());
                String fileName = om.getFileName();
                if (StringUtils.isBlank(fileName) && file != null) {
                    fileName = file.getName();
                }

                // 根据 fileType 分类
                if (DhMaterialType.AVATAR.getValue().equals(fileType)) {
                    // 形象信息
                    PortalOrderDetailVo.PortalAvatarInfo avatarInfo = new PortalOrderDetailVo.PortalAvatarInfo();
                    avatarInfo.setAvatarId(om.getMaterialId());
                    avatarInfo.setName(fileName);
                    avatarInfo.setImageUrl(fileUrl);
                    vo.setAvatarInfo(avatarInfo);
                } else if (DhMaterialType.VOICE.getValue().equals(fileType)) {
                    // 音色信息
                    PortalOrderDetailVo.PortalVoiceInfo voiceInfo = new PortalOrderDetailVo.PortalVoiceInfo();
                    voiceInfo.setVoiceId(om.getMaterialId());
                    voiceInfo.setName(fileName);
                    voiceInfo.setSampleUrl(fileUrl);
                    vo.setVoiceInfo(voiceInfo);
                } else if (DhMaterialType.REFERENCE_VIDEO.getValue().equals(fileType)) {
                    // 参考视频作为普通素材返回
                    PortalOrderDetailVo.PortalOrderMaterialVo mv = new PortalOrderDetailVo.PortalOrderMaterialVo();
                    mv.setMaterialId(om.getMaterialId());
                    mv.setFileName(fileName);
                    mv.setFileType(fileType);
                    mv.setFileUrl(fileUrl);
                    mv.setThumbnailUrl(om.getThumbnailUrl());
                    materials.add(mv);
                } else {
                    // 普通素材
                    PortalOrderDetailVo.PortalOrderMaterialVo mv = new PortalOrderDetailVo.PortalOrderMaterialVo();
                    mv.setMaterialId(om.getMaterialId());
                    mv.setFileName(fileName);
                    mv.setFileType(fileType);
                    mv.setFileUrl(fileUrl);
                    mv.setThumbnailUrl(om.getThumbnailUrl());
                    materials.add(mv);
                }
            }
            vo.setMaterialFiles(materials);
        }

        return vo;
    }

    private String resolveExpectedDelivery(DhOrder order) {
        if (order.getCreateTime() == null || order.getExpectDeliveryHours() == null) {
            return null;
        }
        return DateUtil.formatDateTime(DateUtil.offsetHour(order.getCreateTime(), order.getExpectDeliveryHours()));
    }

    @Override
    public PortalOrderDownloadVo getDownloadInfo(Long userId, Long orderId) {
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在: " + orderId);
        }
        if (!userId.equals(order.getCreateBy())) {
            throw new ServiceException("无权操作该订单");
        }
        if (!DhOrderStatus.COMPLETED.equals(order.getStatus())) {
            throw new ServiceException("作品尚未完成，暂不可下载");
        }
        LambdaQueryWrapper<DhOrderProductionAsset> assetQuery = Wrappers.lambdaQuery();
        assetQuery.eq(DhOrderProductionAsset::getOrderId, orderId);
        assetQuery.last("LIMIT 1");
        DhOrderProductionAsset asset = productionAssetMapper.selectOne(assetQuery);
        if (asset == null || StringUtils.isBlank(asset.getOutputVideoUrl())) {
            throw new ServiceException("成片视频不存在");
        }
        PortalOrderDownloadVo vo = new PortalOrderDownloadVo();
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setDownloadUrl(normalizePortalMediaUrl(asset.getOutputVideoUrl()));
        vo.setFileName(StringUtils.isNotBlank(asset.getOutputVideoName())
            ? asset.getOutputVideoName()
            : order.getOrderNo() + ".mp4");
        vo.setFileSizeMb(asset.getOutputVideoSizeMb());
        vo.setDurationSec(asset.getOutputVideoDurationSec());
        return vo;
    }

    @Override
    public void deleteOrder(Long userId, String userName, Long orderId) {
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在: " + orderId);
        }
        if (!userId.equals(order.getCreateBy())) {
            throw new ServiceException("无权操作该订单");
        }
        DhOrderStatus status = order.getStatus();
        if (DhOrderStatus.PENDING.equals(status)
            || DhOrderStatus.PROCESSING.equals(status)
            || DhOrderStatus.TO_UPLOAD.equals(status)
            || DhOrderStatus.REDO.equals(status)) {
            throw new ServiceException("订单进行中，无法删除，请先取消");
        }
        orderMapper.deleteById(orderId);
        orderCreationHelper.recordProcessLog(orderId, userName, "用户删除作品");
    }

    private String normalizePortalMediaUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        int downloadPathIndex = url.indexOf("/resource/oss/download/");
        return downloadPathIndex >= 0 ? url.substring(downloadPathIndex) : url;
    }

    private List<PortalOrderDetailVo.PortalProgressNodeVo> buildProgressNodes(List<DhProcessLogVo> processLogs, DhOrderStatus orderStatus) {
        if (processLogs == null || processLogs.isEmpty()) {
            return Collections.emptyList();
        }

        List<DhProcessLogVo> sortedLogs = new ArrayList<>(processLogs);
        sortedLogs.sort(Comparator.comparing(DhProcessLogVo::getTime, Comparator.nullsLast(Date::compareTo)));

        boolean terminalStatus = DhOrderStatus.COMPLETED.equals(orderStatus)
            || DhOrderStatus.CANCELLED.equals(orderStatus)
            || DhOrderStatus.REJECTED.equals(orderStatus);
        int currentIndex = terminalStatus ? -1 : sortedLogs.size() - 1;

        List<PortalOrderDetailVo.PortalProgressNodeVo> nodes = new ArrayList<>(sortedLogs.size());
        for (int i = 0; i < sortedLogs.size(); i++) {
            DhProcessLogVo log = sortedLogs.get(i);
            PortalOrderDetailVo.PortalProgressNodeVo node = new PortalOrderDetailVo.PortalProgressNodeVo();
            node.setLabel(log.getAction());
            node.setOperatorName(log.getOperator());
            node.setTime(log.getTime());
            node.setTimestamp(log.getTime());
            node.setDescription(log.getOperator());
            boolean isCurrent = i == currentIndex;
            node.setIsCurrent(isCurrent);
            if (isCurrent) {
                node.setStatus("active");
            } else if (log.getTime() != null) {
                node.setStatus("completed");
            } else {
                node.setStatus("pending");
            }
            nodes.add(node);
        }
        return nodes;
    }
}
