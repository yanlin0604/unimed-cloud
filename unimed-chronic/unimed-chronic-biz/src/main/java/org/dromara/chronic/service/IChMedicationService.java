package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDrugInteractionBo;
import org.dromara.chronic.domain.bo.ChMedicationAdjustBo;
import org.dromara.chronic.domain.bo.ChMedicationRecordBo;
import org.dromara.chronic.domain.vo.ChDrugInteractionVo;
import org.dromara.chronic.domain.vo.ChMedicationAdjustVo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 用药管理服务
 *
 * @author unimed
 */
public interface IChMedicationService {

    List<ChMedicationRecordVo> queryMedicationList(Long patientId);

    Boolean addMedication(ChMedicationRecordBo bo);

    Boolean stopMedication(Long medId, String reason);

    List<ChMedicationAdjustVo> queryAdjustList(Long patientId);

    Long recordAdjust(ChMedicationAdjustBo bo);

    String queryCompliance(Long patientId);

    TableDataInfo<ChDrugInteractionVo> queryInteractionPage(ChDrugInteractionBo bo, PageQuery pageQuery);

    Boolean createInteractionRule(ChDrugInteractionBo bo);

    DrugInteractionCheckVo checkInteraction(Long patientId, String targetDrugCode);
}
