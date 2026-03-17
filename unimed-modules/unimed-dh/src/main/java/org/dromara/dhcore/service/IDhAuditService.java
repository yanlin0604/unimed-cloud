package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhAuditLogQueryBo;
import org.dromara.dhcore.domain.vo.DhAuditLogVo;

/**
 * 数字人口播审计日志服务接�? */
public interface IDhAuditService {

    /**
     * 分页查询审计日志
     */
    TableDataInfo<DhAuditLogVo> queryAuditLogPage(DhAuditLogQueryBo bo, PageQuery pageQuery);
}
