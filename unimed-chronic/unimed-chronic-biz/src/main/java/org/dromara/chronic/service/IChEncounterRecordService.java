package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.bo.ChEncounterDiagnosisBo;
import org.dromara.chronic.domain.entity.ChEncounterRecord;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 诊疗记录服务接口
 *
 * @author unimed
 */
public interface IChEncounterRecordService {

    Long saveDraft(ChEncounterRecordBo bo, List<ChEncounterDiagnosisBo> diagnosisList);

    Long submit(Long encounterId);

    ChEncounterRecord getById(Long encounterId);

    ChEncounterRecordVo queryById(Long encounterId);

    TableDataInfo<ChEncounterRecordVo> queryPageList(ChEncounterRecordBo bo, PageQuery pageQuery);

    Long updateById(ChEncounterRecordBo bo, List<ChEncounterDiagnosisBo> diagnosisList);

    ChEncounterRecord queryBySourceBizNo(String sourceBizNo, Long patientId);
}