package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChAuditLogBo;
import org.dromara.chronic.domain.vo.ChAuditLogVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 审计日志服务
 *
 * @author unimed
 */
public interface IChAuditLogService {

    Long insertByBo(ChAuditLogBo bo);

    ChAuditLogVo queryById(Long id);

    TableDataInfo<ChAuditLogVo> queryPageList(ChAuditLogBo bo, PageQuery pageQuery);
}
