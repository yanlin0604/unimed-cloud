package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.dhcore.domain.DhAuditLog;
import org.dromara.dhcore.domain.DhMemberConfig;
import org.dromara.dhcore.domain.DhOrder;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.DhWalletLog;
import org.dromara.dhcore.domain.bo.DhBalanceAdjustBo;
import org.dromara.dhcore.domain.bo.DhUserQueryBo;
import org.dromara.dhcore.domain.bo.DhUserStatusBo;
import org.dromara.dhcore.domain.bo.DhWalletLogQueryBo;
import org.dromara.dhcore.domain.vo.DhOrderItemVo;
import org.dromara.dhcore.domain.vo.DhUserDetailVo;
import org.dromara.dhcore.domain.vo.DhUserItemVo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;
import org.dromara.dhcore.mapper.DhAuditLogMapper;
import org.dromara.dhcore.mapper.DhMemberConfigMapper;
import org.dromara.dhcore.mapper.DhOrderMapper;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.mapper.DhWalletLogMapper;
import org.dromara.dhcore.service.IDhUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 数字人口播用户管理服务实现
 */
@RequiredArgsConstructor
@Service
public class DhUserServiceImpl implements IDhUserService {

    private final DhUserProfileMapper userProfileMapper;
    private final DhWalletLogMapper walletLogMapper;
    private final DhAuditLogMapper auditLogMapper;
    private final DhMemberConfigMapper memberConfigMapper;
    private final DhOrderMapper orderMapper;

    @Override
    public TableDataInfo<DhUserItemVo> queryUserPage(DhUserQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhUserProfile> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper ->
                wrapper.like(DhUserProfile::getUserName, bo.getKeyword())
                    .or().like(DhUserProfile::getPhone, bo.getKeyword())
            );
        }
        lqw.eq(StringUtils.isNotBlank(bo.getMemberLevel()), DhUserProfile::getMemberLevel, bo.getMemberLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhUserProfile::getStatus, bo.getStatus());
        lqw.orderByDesc(DhUserProfile::getRegisterTime);

        Page<DhUserProfile> page = userProfileMapper.selectPage(pageQuery.build(), lqw);
        List<DhUserItemVo> rows = page.getRecords().stream().map(this::toUserItemVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    public DhUserDetailVo queryUserDetail(Long userId) {
        DhUserProfile user = requireUser(userId);
        DhUserDetailVo detailVo = toUserDetailVo(user);

        List<DhOrder> orderList = orderMapper.selectList(
            Wrappers.<DhOrder>lambdaQuery()
                .eq(DhOrder::getApplicantName, user.getUserName())
                .orderByDesc(DhOrder::getCreateTime)
                .last("limit 5")
        );
        detailVo.setRecentOrders(orderList.stream().map(this::toOrderItemVo).toList());
        detailVo.setNextLevelGap(calcNextLevelGap(user));
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhUserItemVo changeUserStatus(DhUserStatusBo bo) {
        DhUserProfile user = requireUser(bo.getUserId());
        if (StringUtils.equals(user.getStatus(), bo.getStatus())) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重试");
        }
        user.setStatus(bo.getStatus());
        userProfileMapper.updateById(user);

        String operatorName = resolveOperatorName();
        String action = StringUtils.equals("1", bo.getStatus()) ? "USER_DISABLE" : "USER_ENABLE";
        String detail = StringUtils.equals("1", bo.getStatus())
            ? String.format("禁用用户 %s", user.getUserName())
            : String.format("启用用户 %s", user.getUserName());
        insertAuditLog(action, operatorName, "USER", String.valueOf(user.getUserId()), detail);
        return toUserItemVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhUserItemVo adjustBalance(DhBalanceAdjustBo bo) {
        DhUserProfile user = requireUser(bo.getUserId());
        BigDecimal newBalance = nvl(user.getWalletBalance()).add(bo.getAmount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("余额不足，无法扣减");
        }
        user.setWalletBalance(newBalance);
        userProfileMapper.updateById(user);

        String operatorName = resolveOperatorName();
        insertWalletLog(user, bo.getAmount(), operatorName, bo.getReason());
        insertAuditLog("BALANCE_ADJUST", operatorName, "USER", String.valueOf(user.getUserId()),
            String.format("调整余额 %s，原因：%s", bo.getAmount().toPlainString(), bo.getReason()));
        return toUserItemVo(user);
    }

    @Override
    public TableDataInfo<DhWalletLogVo> queryWalletLogPage(DhWalletLogQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhWalletLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, DhWalletLog::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DhWalletLog::getType, bo.getType());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhWalletLog::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhWalletLog::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhWalletLog::getCreateTime);

        Page<DhWalletLog> page = walletLogMapper.selectPage(pageQuery.build(), lqw);
        List<DhWalletLogVo> rows = page.getRecords().stream().map(this::toWalletLogVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    private DhUserProfile requireUser(Long userId) {
        DhUserProfile user = userProfileMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    private BigDecimal calcNextLevelGap(DhUserProfile user) {
        String nextLevel = null;
        if (StringUtils.equals("NORMAL", user.getMemberLevel())) {
            nextLevel = "VIP";
        } else if (StringUtils.equals("VIP", user.getMemberLevel())) {
            nextLevel = "SVIP";
        }
        if (nextLevel == null) {
            return BigDecimal.ZERO;
        }
        DhMemberConfig memberConfig = memberConfigMapper.selectOne(
            Wrappers.<DhMemberConfig>lambdaQuery().eq(DhMemberConfig::getLevel, nextLevel)
        );
        if (memberConfig == null || memberConfig.getMinTopupAmount() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal gap = memberConfig.getMinTopupAmount().subtract(nvl(user.getTotalTopup()));
        return gap.compareTo(BigDecimal.ZERO) > 0 ? gap : BigDecimal.ZERO;
    }

    private void insertWalletLog(DhUserProfile user, BigDecimal amount, String operatorName, String reason) {
        DhWalletLog walletLog = new DhWalletLog();
        walletLog.setUserId(user.getUserId());
        walletLog.setUserName(user.getUserName());
        walletLog.setType("ADJUST");
        walletLog.setAmount(amount);
        walletLog.setBalanceAfter(user.getWalletBalance());
        walletLog.setOperatorName(operatorName);
        walletLog.setRemark(reason);
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

    private DhUserItemVo toUserItemVo(DhUserProfile user) {
        DhUserItemVo vo = new DhUserItemVo();
        vo.setId(user.getUserId());
        vo.setUserName(user.getUserName());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setMemberLevel(user.getMemberLevel());
        vo.setWalletBalance(user.getWalletBalance());
        vo.setTotalTopup(user.getTotalTopup());
        vo.setTotalConsume(user.getTotalConsume());
        vo.setOrderCount(user.getOrderCount());
        vo.setStatus(user.getStatus());
        vo.setRegisterTime(user.getRegisterTime());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }

    private DhUserDetailVo toUserDetailVo(DhUserProfile user) {
        DhUserDetailVo detailVo = new DhUserDetailVo();
        detailVo.setId(user.getUserId());
        detailVo.setUserName(user.getUserName());
        detailVo.setPhone(user.getPhone());
        detailVo.setAvatar(user.getAvatar());
        detailVo.setMemberLevel(user.getMemberLevel());
        detailVo.setWalletBalance(user.getWalletBalance());
        detailVo.setTotalTopup(user.getTotalTopup());
        detailVo.setTotalConsume(user.getTotalConsume());
        detailVo.setOrderCount(user.getOrderCount());
        detailVo.setStatus(user.getStatus());
        detailVo.setRegisterTime(user.getRegisterTime());
        detailVo.setCreateTime(user.getCreateTime());
        detailVo.setUpdateTime(user.getUpdateTime());
        return detailVo;
    }

    private DhOrderItemVo toOrderItemVo(DhOrder order) {
        DhOrderItemVo vo = new DhOrderItemVo();
        vo.setId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setApplicantName(order.getApplicantName());
        vo.setMemberLevel(order.getMemberLevel());
        vo.setStatus(order.getStatus() != null ? order.getStatus().getValue() : null);
        vo.setIsRedo(order.getIsRedo() != null && order.getIsRedo() == 1);
        vo.setPriority(order.getPriority());
        vo.setAssigneeName(order.getAssigneeName());
        vo.setExpectDeliveryHours(order.getExpectDeliveryHours());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        return vo;
    }

    private DhWalletLogVo toWalletLogVo(DhWalletLog log) {
        DhWalletLogVo vo = new DhWalletLogVo();
        vo.setId(log.getLogId());
        vo.setUserId(log.getUserId());
        vo.setUserName(log.getUserName());
        vo.setType(log.getType());
        vo.setAmount(log.getAmount());
        vo.setBalanceAfter(log.getBalanceAfter());
        vo.setRelatedOrderId(log.getRelatedOrderId());
        vo.setOperatorName(log.getOperatorName());
        vo.setRemark(log.getRemark());
        vo.setCreateTime(log.getCreateTime());
        vo.setUpdateTime(log.getUpdateTime());
        return vo;
    }
}
