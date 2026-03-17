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
import org.dromara.dhcore.domain.DhReportTicket;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.bo.DhPunishBo;
import org.dromara.dhcore.domain.bo.DhReportHandleBo;
import org.dromara.dhcore.domain.bo.DhReportQueryBo;
import org.dromara.dhcore.domain.vo.DhReportItemVo;
import org.dromara.dhcore.mapper.DhAuditLogMapper;
import org.dromara.dhcore.mapper.DhReportTicketMapper;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.service.IDhReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 数字人口播举报管理服务实�? */
@RequiredArgsConstructor
@Service
public class DhReportServiceImpl implements IDhReportService {

    private final DhReportTicketMapper reportTicketMapper;
    private final DhUserProfileMapper userProfileMapper;
    private final DhAuditLogMapper auditLogMapper;

    @Override
    public TableDataInfo<DhReportItemVo> queryReportPage(DhReportQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhReportTicket> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper ->
                wrapper.like(DhReportTicket::getReporterName, bo.getKeyword())
                    .or().like(DhReportTicket::getTargetUserName, bo.getKeyword())
                    .or().like(DhReportTicket::getDescription, bo.getKeyword())
            );
        }
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DhReportTicket::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhReportTicket::getStatus, bo.getStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhReportTicket::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhReportTicket::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhReportTicket::getCreateTime);

        Page<DhReportTicket> page = reportTicketMapper.selectPage(pageQuery.build(), lqw);
        List<DhReportItemVo> rows = page.getRecords().stream().map(this::toReportItemVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhReportItemVo handleReport(DhReportHandleBo bo) {
        DhReportTicket reportTicket = requireReport(bo.getReportId());
        if (!StringUtils.equals("PENDING", reportTicket.getStatus())) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重�?);
        }
        String operatorName = resolveOperatorName();
        Date now = new Date();
        reportTicket.setStatus(StringUtils.equals("CONFIRMED", bo.getResult()) ? "CONFIRMED" : "DISMISSED");
        reportTicket.setHandlerName(operatorName);
        reportTicket.setHandleResult(bo.getHandleResult());
        reportTicket.setHandleTime(now);
        reportTicketMapper.updateById(reportTicket);
        return toReportItemVo(reportTicket);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean punishUser(DhPunishBo bo) {
        DhUserProfile user = requireUser(bo.getUserId());
        String operatorName = resolveOperatorName();

        if (StringUtils.equals("BAN", bo.getPunishType())) {
            user.setStatus("1");
            userProfileMapper.updateById(user);
        }

        if (bo.getReportId() != null) {
            DhReportTicket reportTicket = reportTicketMapper.selectById(bo.getReportId());
            if (reportTicket != null && StringUtils.equals("PENDING", reportTicket.getStatus())) {
                reportTicket.setStatus("CONFIRMED");
                reportTicket.setHandlerName(operatorName);
                reportTicket.setHandleResult("处罚生效");
                reportTicket.setHandleTime(new Date());
                reportTicketMapper.updateById(reportTicket);
            }
        }

        String detail = String.format("处罚用户 %s�?s%s，原因：%s",
            user.getUserName(),
            bo.getPunishType(),
            bo.getRestrictDays() == null ? "" : " " + bo.getRestrictDays() + "�?,
            bo.getReason()
        );
        insertAuditLog("USER_PUNISH", operatorName, "USER", String.valueOf(user.getUserId()), detail);
        return Boolean.TRUE;
    }

    private DhReportTicket requireReport(Long reportId) {
        DhReportTicket reportTicket = reportTicketMapper.selectById(reportId);
        if (reportTicket == null) {
            throw new ServiceException("举报记录不存�?);
        }
        return reportTicket;
    }

    private DhUserProfile requireUser(Long userId) {
        DhUserProfile user = userProfileMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存�?);
        }
        return user;
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
        return "当前管理�?;
    }

    private DhReportItemVo toReportItemVo(DhReportTicket reportTicket) {
        DhReportItemVo vo = new DhReportItemVo();
        vo.setId(reportTicket.getReportId());
        vo.setReporterName(reportTicket.getReporterName());
        vo.setTargetUserId(reportTicket.getTargetUserId());
        vo.setTargetUserName(reportTicket.getTargetUserName());
        vo.setTargetContentId(reportTicket.getTargetContentId());
        vo.setTargetContentType(reportTicket.getTargetContentType());
        vo.setType(reportTicket.getType());
        vo.setDescription(reportTicket.getDescription());
        vo.setStatus(reportTicket.getStatus());
        vo.setHandlerName(reportTicket.getHandlerName());
        vo.setHandleResult(reportTicket.getHandleResult());
        vo.setHandleTime(reportTicket.getHandleTime());
        vo.setCreateTime(reportTicket.getCreateTime());
        vo.setUpdateTime(reportTicket.getUpdateTime());
        return vo;
    }
}
