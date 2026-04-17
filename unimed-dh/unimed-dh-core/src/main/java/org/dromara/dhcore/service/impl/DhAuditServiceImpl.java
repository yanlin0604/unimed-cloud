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
import org.dromara.dhcore.domain.DhAuditLog;
import org.dromara.dhcore.domain.bo.DhAuditLogQueryBo;
import org.dromara.dhcore.domain.vo.DhAuditLogVo;
import org.dromara.dhcore.mapper.DhAuditLogMapper;
import org.dromara.dhcore.service.IDhAuditService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 数字人口播审计日志服务实现
 */
@RequiredArgsConstructor
@Service
public class DhAuditServiceImpl implements IDhAuditService {

    private final DhAuditLogMapper auditLogMapper;

    @Override
    public TableDataInfo<DhAuditLogVo> queryAuditLogPage(DhAuditLogQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhAuditLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getAction()), DhAuditLog::getAction, bo.getAction());
        lqw.like(StringUtils.isNotBlank(bo.getOperatorName()), DhAuditLog::getOperatorName, bo.getOperatorName());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhAuditLog::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhAuditLog::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhAuditLog::getCreateTime);

        Page<DhAuditLog> page = auditLogMapper.selectPage(pageQuery.build(), lqw);
        List<DhAuditLogVo> rows = page.getRecords().stream().map(this::toAuditLogVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
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

    private DhAuditLogVo toAuditLogVo(DhAuditLog log) {
        DhAuditLogVo vo = new DhAuditLogVo();
        vo.setId(log.getLogId());
        vo.setAction(log.getAction());
        vo.setOperatorName(log.getOperatorName());
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setDetail(log.getDetail());
        vo.setIpAddress(log.getIpAddress());
        vo.setCreateTime(log.getCreateTime());
        vo.setUpdateTime(log.getUpdateTime());
        return vo;
    }
}
