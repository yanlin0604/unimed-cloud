package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChScreeningBatchBo;
import org.dromara.chronic.domain.vo.ChScreeningBatchVo;

/**
 * 义诊筛查批次服务
 *
 * @author unimed
 */
public interface IChScreeningBatchService {

    ChScreeningBatchVo queryById(Long batchId);

    Boolean insertByBo(ChScreeningBatchBo bo);
}
