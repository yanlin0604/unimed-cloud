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

    /**
     * 修改筛查批次
     *
     * @param bo 批次业务对象（batchId 必填）
     * @return 是否成功
     */
    Boolean updateByBo(ChScreeningBatchBo bo);

    /**
     * 流转批次状态
     *
     * @param batchId 批次ID
     * @param status  目标状态，必须在 PLANNED/ONGOING/FINISHED/CANCELED 之内
     */
    Void updateStatus(Long batchId, String status);
}
