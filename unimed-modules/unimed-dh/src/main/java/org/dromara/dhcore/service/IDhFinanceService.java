package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhFinanceDetailQueryBo;
import org.dromara.dhcore.domain.bo.DhFinanceSummaryQueryBo;
import org.dromara.dhcore.domain.vo.DhFinanceSummaryVo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;

/**
 * 数字人口播财务报表服务接�? */
public interface IDhFinanceService {

    /**
     * 查询财务汇�?     */
    DhFinanceSummaryVo querySummary(DhFinanceSummaryQueryBo bo);

    /**
     * 分页查询财务明细
     */
    TableDataInfo<DhWalletLogVo> queryDetailPage(DhFinanceDetailQueryBo bo, PageQuery pageQuery);
}
