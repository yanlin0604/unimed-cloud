package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChAuditLogBo;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.vo.ChAuditLogVo;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.service.IChAuditLogService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 审计日志服务实现
 * <p>
 * 审计日志为只写+查询模式，不支持修改和删除
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChAuditLogServiceImpl implements IChAuditLogService {

    private final ChAuditLogMapper auditLogMapper;

    @Override
    public Long insertByBo(ChAuditLogBo bo) {
        ChAuditLog entity = MapstructUtils.convert(bo, ChAuditLog.class);
        if (entity.getOperationTime() == null) {
            entity.setOperationTime(new Date());
        }
        auditLogMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public ChAuditLogVo queryById(Long id) {
        return auditLogMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<ChAuditLogVo> queryPageList(ChAuditLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChAuditLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getOperationType()), ChAuditLog::getOperationType, bo.getOperationType());
        lqw.like(StringUtils.isNotBlank(bo.getOperationTarget()), ChAuditLog::getOperationTarget, bo.getOperationTarget());
        lqw.eq(ObjectUtil.isNotNull(bo.getOperatorId()), ChAuditLog::getOperatorId, bo.getOperatorId());
        lqw.orderByDesc(ChAuditLog::getOperationTime);
        Page<ChAuditLogVo> page = auditLogMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }
}
