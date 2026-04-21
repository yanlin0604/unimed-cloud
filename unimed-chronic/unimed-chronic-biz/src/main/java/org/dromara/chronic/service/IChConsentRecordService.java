package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChConsentRecordBo;
import org.dromara.chronic.domain.vo.ChConsentRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 知情同意记录服务
 *
 * @author unimed
 */
public interface IChConsentRecordService {

    Long insertByBo(ChConsentRecordBo bo);

    Boolean updateByBo(ChConsentRecordBo bo);

    ChConsentRecordVo queryById(Long consentId);

    TableDataInfo<ChConsentRecordVo> queryPageList(ChConsentRecordBo bo, PageQuery pageQuery);

    List<ChConsentRecordVo> queryByPatientId(Long patientId);

    Boolean deleteById(Long consentId);
}
