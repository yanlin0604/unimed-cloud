package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMedicationAdjustBo;
import org.dromara.chronic.domain.bo.ChMedicationRecordBo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.chronic.service.IChMedicationService;
import org.springframework.stereotype.Service;

/**
 * 用药管理编排层
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class MedicationManager {

    private final IChMedicationService medicationService;

    public boolean addMedication(ChMedicationRecordBo bo) {
        if (bo.getDrugCode() != null) {
            DrugInteractionCheckVo checkVo = medicationService.checkInteraction(bo.getPatientId(), bo.getDrugCode());
            if (checkVo.isConflict()) {
                throw new org.dromara.common.core.exception.ServiceException("存在药物相互作用风险：" + checkVo.getDescription());
            }
        }
        return medicationService.addMedication(bo);
    }

    public Long adjustMedication(ChMedicationAdjustBo bo) {
        return medicationService.recordAdjust(bo);
    }
}
