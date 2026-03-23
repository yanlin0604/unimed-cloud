package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.dhcore.domain.*;
import org.dromara.dhcore.domain.bo.*;
import org.dromara.dhcore.domain.vo.*;
import org.dromara.dhcore.mapper.*;
import org.dromara.dhcore.service.IDhOrderService;
import org.dromara.dhcore.support.enums.DhOrderStatus;
import org.dromara.dhcore.service.support.DhOrderStatusMachine;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 数字人口播订单与生产服务实现
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DhOrderServiceImpl implements IDhOrderService {

    private final DhOrderMapper orderMapper;
    private final DhOrderMaterialMapper orderMaterialMapper;
    private final DhOrderProcessLogMapper orderProcessLogMapper;
    private final DhOrderProductionAssetMapper orderProductionAssetMapper;
    private final DhOrderQcSnapshotMapper orderQcSnapshotMapper;
    private final RemoteFileService remoteFileService;

    @Override
    public TableDataInfo<DhOrderItemVo> queryOrderPage(DhOrderQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhOrder> lqw = buildOrderQuery(bo);
        Page<DhOrder> page = orderMapper.selectPage(pageQuery.build(), lqw);
        List<DhOrderItemVo> rows = page.getRecords().stream().map(this::toOrderItemVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    public DhOrderDetailVo queryOrderDetail(Long orderId) {
        DhOrder order = requireOrder(orderId);
        DhOrderDetailVo detailVo = toOrderDetailVo(order);

        List<DhOrderMaterial> materialList = orderMaterialMapper.selectList(
            Wrappers.<DhOrderMaterial>lambdaQuery()
                .eq(DhOrderMaterial::getOrderId, orderId)
                .orderByAsc(DhOrderMaterial::getSort)
        );
        // 批量获取 OSS 预签名 URL
        Map<String, RemoteFile> fileMap = new HashMap<>();
        if (materialList != null && !materialList.isEmpty()) {
            List<String> fileIds = materialList.stream()
                .map(DhOrderMaterial::getFileId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();
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
        }
        detailVo.setMaterialFiles(materialList.stream().map(m -> toMaterialFileVo(m, fileMap)).toList());

        List<DhOrderProcessLog> processLogs = orderProcessLogMapper.selectList(
            Wrappers.<DhOrderProcessLog>lambdaQuery()
                .eq(DhOrderProcessLog::getOrderId, orderId)
                .orderByDesc(DhOrderProcessLog::getOperateTime)
        );
        detailVo.setProcessLogs(processLogs.stream().map(this::toProcessLogVo).toList());

        DhOrderProductionAsset asset = queryOrderAsset(orderId);
        detailVo.setProductionAsset(toAssetVo(asset));

        DhOrderQcSnapshot qcSnapshot = queryOrderQcSnapshot(orderId);
        detailVo.setQcChecklist(toQcChecklistVo(qcSnapshot));
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhOrderDetailVo claimOrder(Long orderId, DhOrderClaimBo bo) {
        DhOrder order = requireOrder(orderId);
        String operatorName = resolveOperatorName(bo == null ? null : bo.getOperatorName());

        DhOrderStatusMachine.assertTransition(order.getStatus(), DhOrderStatus.PROCESSING);
        Date now = new Date();
        order.setStatus(DhOrderStatus.PROCESSING);
        order.setAssigneeName(operatorName);
        order.setClaimTime(now);
        orderMapper.updateById(order);

        insertProcessLog(orderId, "运营领取订单", operatorName, now);
        return queryOrderDetail(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhOrderDetailVo cancelOrder(DhOrderCancelBo bo) {
        DhOrder order = requireOrder(bo.getOrderId());
        DhOrderStatusMachine.assertTransition(order.getStatus(), DhOrderStatus.CANCELLED);
        String operatorName = resolveOperatorName(null);
        Date now = new Date();

        order.setStatus(DhOrderStatus.CANCELLED);
        order.setCancelReason(bo.getReason());
        orderMapper.updateById(order);

        insertProcessLog(order.getOrderId(), "订单已取消：" + bo.getReason(), operatorName, now);
        return queryOrderDetail(order.getOrderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhOrderDetailVo rejectOrder(DhOrderRejectBo bo) {
        DhOrder order = requireOrder(bo.getOrderId());
        DhOrderStatusMachine.assertTransition(order.getStatus(), DhOrderStatus.REJECTED);
        String operatorName = resolveOperatorName(null);
        Date now = new Date();

        order.setStatus(DhOrderStatus.REJECTED);
        order.setRejectType(bo.getViolationType());
        order.setRejectReason(bo.getReason());
        orderMapper.updateById(order);

        insertProcessLog(order.getOrderId(), String.format("订单已拒绝(%s)：%s", bo.getViolationType(), bo.getReason()), operatorName, now);
        return queryOrderDetail(order.getOrderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhProductionAssetVo startProduction(Long orderId, DhProductionStartBo bo) {
        DhOrder order = requireOrder(orderId);
        String operatorName = resolveOperatorName(bo == null ? null : bo.getOperatorName());
        Date now = new Date();

        if (StringUtils.isBlank(order.getAssigneeName())) {
            DhOrderStatusMachine.assertTransition(order.getStatus(), DhOrderStatus.PROCESSING);
            order.setStatus(DhOrderStatus.PROCESSING);
            order.setAssigneeName(operatorName);
            order.setClaimTime(now);
            orderMapper.updateById(order);
            insertProcessLog(orderId, "运营领取订单", operatorName, now);
        } else if (!Objects.equals(order.getStatus(), DhOrderStatus.PROCESSING)) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重试");
        }

        DhOrderProductionAsset asset = getOrCreateAsset(orderId);
        if (StringUtils.isBlank(asset.getOperatorName())) {
            asset.setOperatorName(operatorName);
            orderProductionAssetMapper.updateById(asset);
        }

        insertProcessLog(orderId, "开始视频制作", operatorName, now);
        return toAssetVo(asset);
    }

    @Override
    public DhProductionAssetVo getProductionAsset(Long orderId) {
        requireOrder(orderId);
        return toAssetVo(queryOrderAsset(orderId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhProductionAssetVo saveGenerationMeta(Long orderId, DhProductionMetaBo bo) {
        requireOrder(orderId);
        DhOrderProductionAsset asset = getOrCreateAsset(orderId);

        asset.setGenerationChannel(bo.getGenerationChannel());
        asset.setGenerationRef(bo.getGenerationRef());
        asset.setOperatorName(resolveOperatorName(bo.getOperatorName()));
        asset.setRemark(bo.getRemark());
        orderProductionAssetMapper.updateById(asset);

        return toAssetVo(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhProductionAssetVo uploadResultVideo(Long orderId, DhProductionVideoBo bo) {
        requireOrder(orderId);
        DhOrderProductionAsset asset = getOrCreateAsset(orderId);

        asset.setOutputVideoName(bo.getOutputVideoName());
        asset.setOutputVideoUrl(bo.getOutputVideoUrl());
        asset.setOutputVideoDurationSec(bo.getOutputVideoDurationSec());
        asset.setOutputVideoSizeMb(bo.getOutputVideoSizeMb().setScale(2, RoundingMode.HALF_UP));
        asset.setOperatorName(resolveOperatorName(bo.getOperatorName()));
        orderProductionAssetMapper.updateById(asset);

        insertProcessLog(orderId, "上传成品视频", asset.getOperatorName(), new Date());
        return toAssetVo(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhOrderDetailVo submitOrderResult(DhProductionSubmitBo bo) {
        if (StringUtils.isBlank(bo.getOutputVideoUrl())) {
            throw new ServiceException("请先上传成品视频后再提交");
        }

        DhOrder order = requireOrder(bo.getOrderId());
        DhOrderStatusMachine.assertTransition(order.getStatus(), DhOrderStatus.COMPLETED);
        String operatorName = resolveOperatorName(bo.getOperatorName());
        Date now = new Date();

        order.setStatus(DhOrderStatus.COMPLETED);
        order.setResultVideoUrl(bo.getOutputVideoUrl());
        order.setAssigneeName(operatorName);
        order.setCompletedTime(now);
        orderMapper.updateById(order);

        DhOrderProductionAsset asset = getOrCreateAsset(order.getOrderId());
        asset.setGenerationChannel(bo.getGenerationChannel());
        asset.setGenerationRef(bo.getGenerationRef());
        asset.setOutputVideoName(bo.getOutputVideoName());
        asset.setOutputVideoUrl(bo.getOutputVideoUrl());
        asset.setOutputVideoDurationSec(bo.getOutputVideoDurationSec());
        asset.setOutputVideoSizeMb(bo.getOutputVideoSizeMb().setScale(2, RoundingMode.HALF_UP));
        asset.setOperatorName(operatorName);
        asset.setSubmittedAt(now);
        asset.setRemark(bo.getRemark());
        orderProductionAssetMapper.updateById(asset);

        DhOrderQcSnapshot qcSnapshot = queryOrderQcSnapshot(order.getOrderId());
        if (qcSnapshot == null) {
            qcSnapshot = new DhOrderQcSnapshot();
            qcSnapshot.setOrderId(order.getOrderId());
            qcSnapshot.setLipSync(toInt(bo.getQcChecklist().getLipSync()));
            qcSnapshot.setNoVisualDefect(toInt(bo.getQcChecklist().getNoVisualDefect()));
            qcSnapshot.setScriptMatched(toInt(bo.getQcChecklist().getScriptMatched()));
            qcSnapshot.setDurationOk(toInt(bo.getQcChecklist().getDurationOk()));
            orderQcSnapshotMapper.insert(qcSnapshot);
        } else {
            qcSnapshot.setLipSync(toInt(bo.getQcChecklist().getLipSync()));
            qcSnapshot.setNoVisualDefect(toInt(bo.getQcChecklist().getNoVisualDefect()));
            qcSnapshot.setScriptMatched(toInt(bo.getQcChecklist().getScriptMatched()));
            qcSnapshot.setDurationOk(toInt(bo.getQcChecklist().getDurationOk()));
            orderQcSnapshotMapper.updateById(qcSnapshot);
        }

        insertProcessLog(order.getOrderId(), "提交交付成功", operatorName, now);
        return queryOrderDetail(order.getOrderId());
    }

    private LambdaQueryWrapper<DhOrder> buildOrderQuery(DhOrderQueryBo bo) {
        LambdaQueryWrapper<DhOrder> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper -> wrapper.like(DhOrder::getOrderNo, bo.getKeyword()).or().like(DhOrder::getTitle, bo.getKeyword()));
        }
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhOrder::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getMemberLevel()), DhOrder::getMemberLevel, bo.getMemberLevel());
        lqw.like(StringUtils.isNotBlank(bo.getApplicantName()), DhOrder::getApplicantName, bo.getApplicantName());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhOrder::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhOrder::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhOrder::getPriority, DhOrder::getCreateTime);
        return lqw;
    }

    private DhOrder requireOrder(Long orderId) {
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        return order;
    }

    private DhOrderProductionAsset getOrCreateAsset(Long orderId) {
        DhOrderProductionAsset asset = queryOrderAsset(orderId);
        if (asset != null) {
            return asset;
        }

        DhOrderProductionAsset newAsset = new DhOrderProductionAsset();
        newAsset.setOrderId(orderId);
        newAsset.setGenerationChannel("THIRD_PARTY_MANUAL");
        orderProductionAssetMapper.insert(newAsset);
        return newAsset;
    }

    private DhOrderProductionAsset queryOrderAsset(Long orderId) {
        return orderProductionAssetMapper.selectOne(
            Wrappers.<DhOrderProductionAsset>lambdaQuery().eq(DhOrderProductionAsset::getOrderId, orderId)
        );
    }

    private DhOrderQcSnapshot queryOrderQcSnapshot(Long orderId) {
        return orderQcSnapshotMapper.selectOne(
            Wrappers.<DhOrderQcSnapshot>lambdaQuery().eq(DhOrderQcSnapshot::getOrderId, orderId)
        );
    }

    private void insertProcessLog(Long orderId, String actionText, String operatorName, Date operateTime) {
        DhOrderProcessLog log = new DhOrderProcessLog();
        log.setOrderId(orderId);
        log.setActionText(actionText);
        log.setOperatorName(operatorName);
        log.setOperateTime(operateTime);
        orderProcessLogMapper.insert(log);
    }

    private Date parseDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        try {
            return DateUtil.parseDateTime(dateTime);
        } catch (Exception ex) {
            throw new ServiceException("时间参数格式错误");
        }
    }

    private String resolveOperatorName(String operatorName) {
        if (LoginHelper.isLogin() && StringUtils.isNotBlank(LoginHelper.getUsername())) {
            return LoginHelper.getUsername();
        }
        return "系统";
    }

    private DhOrderItemVo toOrderItemVo(DhOrder order) {
        DhOrderItemVo vo = new DhOrderItemVo();
        vo.setId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setApplicantName(order.getApplicantName());
        vo.setMemberLevel(order.getMemberLevel());
        vo.setStatus(order.getStatus() != null ? order.getStatus().getValue() : null);
        vo.setIsRedo(toBool(order.getIsRedo()));
        vo.setPriority(order.getPriority());
        vo.setAssigneeName(order.getAssigneeName());
        vo.setExpectDeliveryHours(order.getExpectDeliveryHours());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        return vo;
    }

    private DhOrderDetailVo toOrderDetailVo(DhOrder order) {
        DhOrderDetailVo vo = new DhOrderDetailVo();
        vo.setId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setApplicantName(order.getApplicantName());
        vo.setMemberLevel(order.getMemberLevel());
        vo.setStatus(order.getStatus() != null ? order.getStatus().getValue() : null);
        vo.setIsRedo(toBool(order.getIsRedo()));
        vo.setPriority(order.getPriority());
        vo.setAssigneeName(order.getAssigneeName());
        vo.setExpectDeliveryHours(order.getExpectDeliveryHours());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        vo.setScriptText(order.getScriptText());
        vo.setMaterialSummary(order.getMaterialSummary());
        vo.setContactInfo(order.getContactInfo());
        vo.setToneStyle(order.getToneStyle());
        vo.setSceneType(order.getSceneType());
        vo.setSpeechSpeed(order.getSpeechSpeed());
        vo.setVideoRatio(order.getVideoRatio());
        vo.setVideoResolution(order.getVideoResolution());
        vo.setVideoDuration(order.getVideoDuration());
        vo.setOrderAmount(order.getOrderAmount());
        vo.setDiscountRate(order.getDiscountRate());
        vo.setActualAmount(order.getActualAmount());
        vo.setCopyrightDeclared(toBool(order.getCopyrightDeclared()));
        vo.setRedoReason(order.getRedoReason());
        vo.setOriginalOrderId(order.getOriginalOrderId());
        vo.setRedoCount(order.getRedoCount());
        vo.setResultVideoUrl(order.getResultVideoUrl());
        vo.setCancelReason(order.getCancelReason());
        vo.setRejectReason(order.getRejectReason());
        vo.setRejectType(order.getRejectType());
        return vo;
    }

    private DhMaterialFileVo toMaterialFileVo(DhOrderMaterial material, Map<String, RemoteFile> fileMap) {
        DhMaterialFileVo vo = new DhMaterialFileVo();
        vo.setFileId(material.getFileId());
        vo.setFileType(material.getFileType());
        vo.setThumbnailUrl(material.getThumbnailUrl());
        vo.setFileName(material.getFileName());
        RemoteFile remoteFile = fileMap.get(material.getFileId());
        if (remoteFile != null) {
            vo.setFileUrl(remoteFile.getUrl());
        } else {
            vo.setFileUrl(material.getFileUrl());
        }
        return vo;
    }

    private DhProcessLogVo toProcessLogVo(DhOrderProcessLog log) {
        DhProcessLogVo vo = new DhProcessLogVo();
        vo.setTime(log.getOperateTime());
        vo.setAction(log.getActionText());
        vo.setOperator(log.getOperatorName());
        return vo;
    }

    private DhProductionAssetVo toAssetVo(DhOrderProductionAsset asset) {
        if (asset == null) {
            return null;
        }
        DhProductionAssetVo vo = new DhProductionAssetVo();
        vo.setOrderId(asset.getOrderId());
        vo.setGenerationChannel(asset.getGenerationChannel());
        vo.setGenerationRef(asset.getGenerationRef());
        vo.setOutputVideoName(asset.getOutputVideoName());
        vo.setOutputVideoUrl(asset.getOutputVideoUrl());
        vo.setOutputVideoDurationSec(asset.getOutputVideoDurationSec());
        vo.setOutputVideoSizeMb(asset.getOutputVideoSizeMb());
        vo.setOperatorName(asset.getOperatorName());
        vo.setSubmittedAt(asset.getSubmittedAt());
        vo.setRemark(asset.getRemark());
        return vo;
    }

    private DhQcChecklistVo toQcChecklistVo(DhOrderQcSnapshot qcSnapshot) {
        if (qcSnapshot == null) {
            return null;
        }
        DhQcChecklistVo vo = new DhQcChecklistVo();
        vo.setLipSync(toBool(qcSnapshot.getLipSync()));
        vo.setNoVisualDefect(toBool(qcSnapshot.getNoVisualDefect()));
        vo.setScriptMatched(toBool(qcSnapshot.getScriptMatched()));
        vo.setDurationOk(toBool(qcSnapshot.getDurationOk()));
        return vo;
    }

    private static Integer toInt(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }

    private static Boolean toBool(Integer value) {
        return value != null && value == 1;
    }
}
