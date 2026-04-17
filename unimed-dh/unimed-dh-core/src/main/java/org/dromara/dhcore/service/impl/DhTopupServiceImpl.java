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
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.dhcore.domain.DhAuditLog;
import org.dromara.dhcore.domain.DhTopupTicket;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.DhWalletLog;
import org.dromara.dhcore.domain.bo.DhTopupApproveBo;
import org.dromara.dhcore.domain.bo.DhTopupNeedMoreBo;
import org.dromara.dhcore.domain.bo.DhTopupQueryBo;
import org.dromara.dhcore.domain.bo.DhTopupRejectBo;
import org.dromara.dhcore.domain.vo.DhTopupTicketVo;
import org.dromara.dhcore.mapper.DhAuditLogMapper;
import org.dromara.dhcore.mapper.DhTopupTicketMapper;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.mapper.DhWalletLogMapper;
import org.dromara.dhcore.service.IDhTopupService;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数字人口播充值审核服务实现
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class DhTopupServiceImpl implements IDhTopupService {

    private static final Set<String> APPROVE_STATUS_SET = Set.of("PENDING", "NEED_MORE");
    private static final Set<String> NEED_MORE_STATUS_SET = Set.of("PENDING");
    private static final Set<String> REJECT_STATUS_SET = Set.of("PENDING", "NEED_MORE");

    private final DhTopupTicketMapper topupTicketMapper;
    private final DhUserProfileMapper userProfileMapper;
    private final DhWalletLogMapper walletLogMapper;
    private final DhAuditLogMapper auditLogMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @Override
    public TableDataInfo<DhTopupTicketVo> queryTopupPage(DhTopupQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhTopupTicket> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper ->
                wrapper.like(DhTopupTicket::getUserName, bo.getKeyword())
                    .or().like(DhTopupTicket::getVoucherDesc, bo.getKeyword())
            );
        }
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhTopupTicket::getStatus, bo.getStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhTopupTicket::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhTopupTicket::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhTopupTicket::getCreateTime);

        Page<DhTopupTicket> page = topupTicketMapper.selectPage(pageQuery.build(), lqw);
        List<DhTopupTicketVo> rows = page.getRecords().stream().map(this::toTopupTicketVo).toList();
        fillVoucherImageUrls(rows);
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhTopupTicketVo approveTopup(DhTopupApproveBo bo) {
        DhTopupTicket ticket = requireTopupTicket(bo.getTicketId());
        if (!APPROVE_STATUS_SET.contains(ticket.getStatus())) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重试");
        }

        String operatorName = resolveOperatorName();
        Date now = new Date();
        BigDecimal actualAmount = nvl(bo.getActualAmount());

        ticket.setStatus("APPROVED");
        ticket.setActualAmount(actualAmount);
        ticket.setApprovedBy(operatorName);
        ticket.setApprovedAt(now);
        ticket.setRejectReason(null);
        ticket.setRemark(bo.getRemark());
        topupTicketMapper.updateById(ticket);

        DhUserProfile user = requireUser(ticket.getUserId());
        BigDecimal balance = nvl(user.getWalletBalance()).add(actualAmount);
        user.setWalletBalance(balance);
        user.setTotalTopup(nvl(user.getTotalTopup()).add(actualAmount));
        userProfileMapper.updateById(user);

        insertWalletLog(user, "TOPUP", actualAmount, null, operatorName, "充值审核通过");
        insertAuditLog("TOPUP_APPROVE", operatorName, "TOPUP", String.valueOf(ticket.getTicketId()),
            String.format("确认充值 ¥%s", actualAmount.toPlainString()));
        return toTopupTicketVoWithUrls(ticket);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhTopupTicketVo markTopupNeedMore(DhTopupNeedMoreBo bo) {
        DhTopupTicket ticket = requireTopupTicket(bo.getTicketId());
        if (!NEED_MORE_STATUS_SET.contains(ticket.getStatus())) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重试");
        }
        String operatorName = resolveOperatorName();
        ticket.setStatus("NEED_MORE");
        ticket.setRemark(bo.getReason());
        topupTicketMapper.updateById(ticket);

        insertAuditLog("TOPUP_NEED_MORE", operatorName, "TOPUP", String.valueOf(ticket.getTicketId()),
            String.format("充值工单待补充：%s", bo.getReason()));
        return toTopupTicketVoWithUrls(ticket);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhTopupTicketVo rejectTopup(DhTopupRejectBo bo) {
        DhTopupTicket ticket = requireTopupTicket(bo.getTicketId());
        if (!REJECT_STATUS_SET.contains(ticket.getStatus())) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重试");
        }
        String operatorName = resolveOperatorName();
        ticket.setStatus("REJECTED");
        ticket.setRejectReason(bo.getReason());
        topupTicketMapper.updateById(ticket);

        insertAuditLog("TOPUP_REJECT", operatorName, "TOPUP", String.valueOf(ticket.getTicketId()),
            String.format("拒绝充值：%s", bo.getReason()));
        return toTopupTicketVoWithUrls(ticket);
    }

    private DhTopupTicket requireTopupTicket(Long ticketId) {
        DhTopupTicket ticket = topupTicketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new ServiceException("充值工单不存在");
        }
        return ticket;
    }

    private DhUserProfile requireUser(Long userId) {
        DhUserProfile user = userProfileMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    private void insertWalletLog(DhUserProfile user, String type, BigDecimal amount, Long relatedOrderId, String operatorName, String remark) {
        DhWalletLog walletLog = new DhWalletLog();
        walletLog.setUserId(user.getUserId());
        walletLog.setUserName(user.getUserName());
        walletLog.setType(type);
        walletLog.setAmount(amount);
        walletLog.setBalanceAfter(user.getWalletBalance());
        walletLog.setRelatedOrderId(relatedOrderId);
        walletLog.setOperatorName(operatorName);
        walletLog.setRemark(remark);
        walletLogMapper.insert(walletLog);
    }

    private void insertAuditLog(String action, String operatorName, String targetType, String targetId, String detail) {
        DhAuditLog auditLog = new DhAuditLog();
        auditLog.setAction(action);
        auditLog.setOperatorName(operatorName);
        auditLog.setTargetType(targetType);
        auditLog.setTargetId(targetId);
        auditLog.setDetail(detail);
        auditLog.setIpAddress("127.0.0.1");
        auditLogMapper.insert(auditLog);
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

    private String resolveOperatorName() {
        if (LoginHelper.isLogin() && StringUtils.isNotBlank(LoginHelper.getUsername())) {
            return LoginHelper.getUsername();
        }
        return "系统";
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private DhTopupTicketVo toTopupTicketVo(DhTopupTicket ticket) {
        DhTopupTicketVo vo = new DhTopupTicketVo();
        vo.setId(ticket.getTicketId());
        vo.setUserId(ticket.getUserId());
        vo.setUserName(ticket.getUserName());
        vo.setAmount(ticket.getAmount());
        vo.setStatus(ticket.getStatus());
        vo.setPaymentType(ticket.getPaymentType());
        vo.setVoucherDesc(ticket.getVoucherDesc());
        vo.setVoucherImageIds(ticket.getVoucherImageIds());
        vo.setActualAmount(ticket.getActualAmount());
        vo.setApprovedBy(ticket.getApprovedBy());
        vo.setApprovedAt(ticket.getApprovedAt());
        vo.setRejectReason(ticket.getRejectReason());
        vo.setRemark(ticket.getRemark());
        vo.setCreateTime(ticket.getCreateTime());
        vo.setUpdateTime(ticket.getUpdateTime());
        return vo;
    }

    /**
     * 单条转换并解析 OSS URL（用于审核操作返回）
     */
    private DhTopupTicketVo toTopupTicketVoWithUrls(DhTopupTicket ticket) {
        DhTopupTicketVo vo = toTopupTicketVo(ticket);
        fillVoucherImageUrls(List.of(vo));
        return vo;
    }

    /**
     * 批量填充凭证图片 URL（通过 OSS 服务解析）
     */
    private void fillVoucherImageUrls(List<DhTopupTicketVo> voList) {
        // 1. 收集所有 ossId（凭证可能包含多张，逗号分隔）
        List<String> allOssIds = voList.stream()
            .map(DhTopupTicketVo::getVoucherImageIds)
            .filter(StringUtils::isNotBlank)
            .flatMap(ids -> Arrays.stream(ids.split(",")))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (allOssIds.isEmpty()) {
            return;
        }
        // 2. 批量查询 OSS 文件信息
        Map<String, String> ossUrlMap = new HashMap<>();
        try {
            List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", allOssIds));
            if (files != null) {
                files.forEach(f -> {
                    if (f != null && StringUtils.isNotBlank(f.getUrl())) {
                        ossUrlMap.put(String.valueOf(f.getOssId()), f.getUrl());
                    }
                });
            }
        } catch (Exception e) {
            log.warn("批量获取充值凭证OSS文件信息失败: {}", e.getMessage());
        }
        // 3. 回填 URL 列表
        for (DhTopupTicketVo vo : voList) {
            if (StringUtils.isNotBlank(vo.getVoucherImageIds())) {
                List<String> urls = Arrays.stream(vo.getVoucherImageIds().split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .map(ossId -> ossUrlMap.getOrDefault(ossId, ""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
                vo.setVoucherImageUrls(urls);
            }
        }
    }
}
