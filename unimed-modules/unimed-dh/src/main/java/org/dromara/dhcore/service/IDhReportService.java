package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhPunishBo;
import org.dromara.dhcore.domain.bo.DhReportHandleBo;
import org.dromara.dhcore.domain.bo.DhReportQueryBo;
import org.dromara.dhcore.domain.vo.DhReportItemVo;

/**
 * 数字人口播举报管理服务接口
 */
public interface IDhReportService {

    /**
     * 分页查询举报单
     */
    TableDataInfo<DhReportItemVo> queryReportPage(DhReportQueryBo bo, PageQuery pageQuery);

    /**
     * 处理举报
     */
    DhReportItemVo handleReport(DhReportHandleBo bo);

    /**
     * 处罚用户
     */
    Boolean punishUser(DhPunishBo bo);
}
