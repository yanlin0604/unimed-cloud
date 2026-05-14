package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChMedicalExamBo;
import org.dromara.chronic.domain.vo.ChMedicalExamVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 检查记录服务
 *
 * @author unimed
 */
public interface IChMedicalExamService {

    TableDataInfo<ChMedicalExamVo> queryPageList(ChMedicalExamBo bo, PageQuery pageQuery);

    List<ChMedicalExamVo> queryByPatientId(Long patientId);

    ChMedicalExamVo queryById(Long examId);

    Long create(ChMedicalExamBo bo);

    Boolean update(ChMedicalExamBo bo);

    Boolean deleteByIds(java.util.Collection<Long> ids);
}
