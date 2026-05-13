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

    /**
     * 更新生活方式记录
     */
    Void update(ChLifestyleRecordBo bo);

    /**
     * 删除记录
     */
    Void remove(Long id);

    ChLifestyleRecordVo queryById(Long id);

    TableDataInfo<ChLifestyleRecordVo> queryPageList(ChLifestyleRecordBo bo, PageQuery pageQuery);

    List<ChLifestyleRecordVo> queryTrend(Long patientId, Integer limit);

    /**
     * 查询最近一条生活方式记录
     *
     * @param patientId 患者ID
     * @return 最新记录，无则返回 null
     */
    ChLifestyleRecordVo queryLatest(Long patientId);
}
