package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 义诊筛查批次服务
 *
 * @author unimed
 */
public interface IChScreeningBatchService {

    ChScreeningBatchVo queryById(Long batchId);

    TableDataInfo<ChScreeningBatchVo> queryPageList(ChScreeningBatchBo bo, PageQuery pageQuery);

    Boolean insertByBo(ChScreeningBatchBo bo);
}
