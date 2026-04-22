package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMedicationAdjustBo;
import org.dromara.chronic.domain.bo.ChMedicationRecordBo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;

/**
 * 用药管理编排层
 * <p>
 * R14: 新增 previewAdjust 方法，只做相互作用检查返回预览结果，不持久化；
 * adjustMedication 保留 previewConfirmed=true 强校验。
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
                throw new ServiceException("存在药物相互作用风险：" + checkVo.getDescription());
            }
        }
        return medicationService.addMedication(bo);
    }

    /**
     * R14: 用药调整预览 —— 只做相互作用检查，返回 DrugInteractionCheckVo，不持久化
     */
    public DrugInteractionCheckVo previewAdjust(ChMedicationAdjustBo bo) {
        if (bo.getTargetDrugCode() != null) {
            return medicationService.checkInteraction(bo.getPatientId(), bo.getTargetDrugCode());
        }
        // 无目标药品编码时返回空结果
        DrugInteractionCheckVo vo = new DrugInteractionCheckVo();
        vo.setConflict(false);
        vo.setTargetDrugCode(bo.getTargetDrugCode());
        return vo;
    }

    /**
     * R14: 用药调整提交 —— 必须经过 previewConfirmed=true 校验
     */
    public Long adjustMedication(ChMedicationAdjustBo bo) {
        if (!Boolean.TRUE.equals(bo.getPreviewConfirmed())) {
            throw new ServiceException("用药调整必须先预览确认(ADJUST_NOT_CONFIRMED)");
        }
        return medicationService.recordAdjust(bo);
    }
}
