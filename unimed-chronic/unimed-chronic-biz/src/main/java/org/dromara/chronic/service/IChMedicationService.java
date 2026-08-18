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

    TableDataInfo<ChMedicationRecordVo> queryMedicationPage(Long patientId, String status, String drugName,
                                                            PageQuery pageQuery);

    Boolean addMedication(ChMedicationRecordBo bo);

    Boolean stopMedication(Long medId, String reason);

    List<ChMedicationAdjustVo> queryAdjustList(Long patientId);

    TableDataInfo<ChMedicationAdjustVo> queryAdjustPage(Long patientId, String adjustType, PageQuery pageQuery);

    Long recordAdjust(ChMedicationAdjustBo bo);

    String queryCompliance(Long patientId);

    TableDataInfo<ChDrugInteractionVo> queryInteractionPage(ChDrugInteractionBo bo, PageQuery pageQuery);

    Boolean createInteractionRule(ChDrugInteractionBo bo);

    DrugInteractionCheckVo checkInteraction(Long patientId, String targetDrugCode);

    /**
     * 服药打卡
     *
     * @param medId     用药记录ID
     * @param patientId 当前登录患者ID（用于归属校验）
     * @return true
     * @throws ServiceException 药物已停用或不属于当前患者时抛出
     */
    Boolean checkinMedication(Long medId, Long patientId);
}
