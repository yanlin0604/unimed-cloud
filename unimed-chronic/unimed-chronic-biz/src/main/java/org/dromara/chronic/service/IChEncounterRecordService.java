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

    /**
     * 查询患者最近一次诊疗记录（按 encounter_time desc，包含草稿与已提交）
     */
    ChEncounterRecordVo queryLatestByPatientId(Long patientId);

    /**
     * 删除诊疗记录（级联清理诊断子表）。
     * 不在此层做状态校验或审计，调用方（Manager）负责。
     */
    boolean deleteById(Long encounterId);
}