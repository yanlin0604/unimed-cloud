package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChLabTestBo;
import org.dromara.chronic.domain.vo.ChLabTestVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 检验记录服务
 *
 * @author unimed
 */
public interface IChLabTestService {

    TableDataInfo<ChLabTestVo> queryPageList(ChLabTestBo bo, PageQuery pageQuery);

    List<ChLabTestVo> queryByPatientId(Long patientId);

    ChLabTestVo queryById(Long testId);

    Long create(ChLabTestBo bo);

    Boolean update(ChLabTestBo bo);

    Boolean deleteByIds(java.util.Collection<Long> ids);
}
