package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChLifestyleRecordBo;
import org.dromara.chronic.domain.vo.ChLifestyleRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 生活方式记录服务
 *
 * @author unimed
 */
public interface IChLifestyleRecordService {

    Long add(ChLifestyleRecordBo bo);

    ChLifestyleRecordVo queryById(Long id);

    TableDataInfo<ChLifestyleRecordVo> queryPageList(ChLifestyleRecordBo bo, PageQuery pageQuery);

    List<ChLifestyleRecordVo> queryTrend(Long patientId, Integer limit);
}
