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
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.DhWalletLog;
import org.dromara.dhcore.domain.bo.DhFinanceDetailQueryBo;
import org.dromara.dhcore.domain.bo.DhFinanceSummaryQueryBo;
import org.dromara.dhcore.domain.vo.DhFinanceSummaryVo;
import org.dromara.dhcore.domain.vo.DhFinanceTrendVo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.mapper.DhWalletLogMapper;
import org.dromara.dhcore.service.IDhFinanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数字人口播财务报表服务实现
 */
@RequiredArgsConstructor
@Service
public class DhFinanceServiceImpl implements IDhFinanceService {

    private final DhWalletLogMapper walletLogMapper;
    private final DhUserProfileMapper userProfileMapper;

    @Override
    public DhFinanceSummaryVo querySummary(DhFinanceSummaryQueryBo bo) {
        Date[] rangeDates = resolveRange(bo);
        Date start = rangeDates[0];
        Date end = rangeDates[1];

        List<DhWalletLog> logs = walletLogMapper.selectList(
            Wrappers.<DhWalletLog>lambdaQuery()
                .ge(DhWalletLog::getCreateTime, start)
                .le(DhWalletLog::getCreateTime, end)
        );

        BigDecimal totalTopup = sumAmount(logs, "TOPUP", false);
        BigDecimal totalConsume = sumAmount(logs, "CONSUME", true);
        BigDecimal totalRefund = sumAmount(logs, "REFUND", true);
        BigDecimal totalBalance = userProfileMapper.selectList(Wrappers.<DhUserProfile>lambdaQuery()).stream()
            .map(DhUserProfile::getWalletBalance)
            .filter(v -> v != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        DhFinanceSummaryVo vo = new DhFinanceSummaryVo();
        vo.setTotalTopup(scale(totalTopup));
        vo.setTotalConsume(scale(totalConsume));
        vo.setTotalRefund(scale(totalRefund));
        vo.setTotalBalance(scale(totalBalance));
        vo.setTrend(buildTrend(logs, start, end));
        return vo;
    }

    @Override
    public TableDataInfo<DhWalletLogVo> queryDetailPage(DhFinanceDetailQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhWalletLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DhWalletLog::getType, bo.getType());
        lqw.like(StringUtils.isNotBlank(bo.getKeyword()), DhWalletLog::getUserName, bo.getKeyword());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhWalletLog::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhWalletLog::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhWalletLog::getCreateTime);

        Page<DhWalletLog> page = walletLogMapper.selectPage(pageQuery.build(), lqw);
        List<DhWalletLogVo> rows = page.getRecords().stream().map(this::toWalletLogVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    private Date[] resolveRange(DhFinanceSummaryQueryBo bo) {
        Date now = new Date();
        String range = StringUtils.isBlank(bo.getRange()) ? "today" : bo.getRange();
        if ("today".equals(range)) {
            return new Date[]{DateUtil.beginOfDay(now), DateUtil.endOfDay(now)};
        }
        if ("week".equals(range)) {
            return new Date[]{DateUtil.beginOfWeek(now), DateUtil.endOfDay(now)};
        }
        if ("month".equals(range)) {
            return new Date[]{DateUtil.beginOfMonth(now), DateUtil.endOfDay(now)};
        }
        if ("custom".equals(range)) {
            if (StringUtils.isBlank(bo.getBeginTime()) || StringUtils.isBlank(bo.getEndTime())) {
                throw new ServiceException("自定义时间范围不能为空");
            }
            return new Date[]{parseDateTime(bo.getBeginTime()), parseDateTime(bo.getEndTime())};
        }
        throw new ServiceException("不支持的时间范围");
    }

    private BigDecimal sumAmount(List<DhWalletLog> logs, String type, boolean abs) {
        BigDecimal total = logs.stream()
            .filter(log -> StringUtils.equals(type, log.getType()))
            .map(DhWalletLog::getAmount)
            .filter(v -> v != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return abs ? total.abs() : total;
    }

    private List<DhFinanceTrendVo> buildTrend(List<DhWalletLog> logs, Date start, Date end) {
        List<DhFinanceTrendVo> trend = new ArrayList<>();
        Date cursor = DateUtil.beginOfDay(start);
        Date limit = DateUtil.endOfDay(end);
        while (!cursor.after(limit)) {
            Date dayStart = DateUtil.beginOfDay(cursor);
            Date dayEnd = DateUtil.endOfDay(cursor);
            DhFinanceTrendVo point = new DhFinanceTrendVo();
            point.setDate(DateUtil.format(dayStart, "MM-dd"));
            point.setTopup(scale(sumAmount(filterByDay(logs, dayStart, dayEnd), "TOPUP", false)));
            point.setConsume(scale(sumAmount(filterByDay(logs, dayStart, dayEnd), "CONSUME", true)));
            point.setRefund(scale(sumAmount(filterByDay(logs, dayStart, dayEnd), "REFUND", true)));
            trend.add(point);
            cursor = DateUtil.offsetDay(cursor, 1);
        }
        return trend;
    }

    private List<DhWalletLog> filterByDay(List<DhWalletLog> logs, Date dayStart, Date dayEnd) {
        return logs.stream()
            .filter(log -> inRange(log.getCreateTime(), dayStart, dayEnd))
            .toList();
    }

    private boolean inRange(Date value, Date begin, Date end) {
        if (value == null) {
            return false;
        }
        return !value.before(begin) && !value.after(end);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
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
