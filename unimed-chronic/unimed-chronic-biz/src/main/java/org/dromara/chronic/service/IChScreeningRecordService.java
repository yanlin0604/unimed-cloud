package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChScreeningRecordBo;
import org.dromara.chronic.domain.vo.ChScreeningRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 义诊筛查记录服务
 *
 * @author unimed
 */
public interface IChScreeningRecordService {

    ChScreeningRecordVo saveRecord(ChScreeningRecordBo bo);

    List<ChScreeningRecordVo> batchSave(List<ChScreeningRecordBo> list);

    ChScreeningRecordVo queryById(Long recordId);

    List<ChScreeningRecordVo> queryByBatchId(Long batchId);

    TableDataInfo<ChScreeningRecordVo> queryPageList(ChScreeningRecordBo bo, PageQuery pageQuery);
}
