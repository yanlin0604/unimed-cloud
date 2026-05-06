package org.dromara.chronic.manager;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.MedicalDocumentOcrConfirmBo;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrConfirmResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 医疗文档OCR确认入库路由
 *
 * @author unimed
 */
@Component
@RequiredArgsConstructor
public class MedicalDocumentOcrConfirmRouter {

    private final PatientProfileManager patientProfileManager;
    private final HealthMetricManager healthMetricManager;
    private final HealthExamManager healthExamManager;

    @Transactional(rollbackFor = Exception.class)
    public MedicalDocumentOcrConfirmResult confirm(Long taskId, MedicalDocumentOcrConfirmBo bo) {
        MedicalDocumentOcrConfirmResult result = new MedicalDocumentOcrConfirmResult();
        if (shouldConfirm(bo.getConfirmTarget(), "ARCHIVE")) {
            result.setPatientId(confirmArchive(taskId, bo.getProfile(), bo.getDiseases(), bo.getUpdateSupport()));
        }
        Long patientId = result.getPatientId();
        if (patientId == null && bo.getProfile() != null) {
            patientId = bo.getProfile().getPatientId();
        }
        if (shouldConfirm(bo.getConfirmTarget(), "METRIC")) {
            result.setMetricIds(confirmMetrics(patientId, bo.getMetrics()));
        }
        if (shouldConfirm(bo.getConfirmTarget(), "REPORT")) {
            result.setExamId(confirmReport(patientId, bo.getExam(), bo.getReportItems()));
        }
        return result;
    }

    public Long confirmArchive(Long taskId, ChPatientProfileBo profile, List<ChPatientDiseaseBo> diseases, Boolean updateSupport) {
        if (profile == null) {
            throw new ServiceException("建档确认数据不能为空");
        }
        Long patientId = profile.getPatientId();
        if (patientId == null && StringUtils.isNotBlank(profile.getIdCard())) {
            ChPatientProfile existed = patientProfileManager.findByIdCard(profile.getIdCard());
            if (existed != null) {
                patientId = existed.getPatientId();
            }
        }
        if (patientId != null && Boolean.TRUE.equals(updateSupport)) {
            patientProfileManager.updateArchive(profile, patientId);
        } else if (patientId != null && !Boolean.TRUE.equals(updateSupport)) {
            throw new ServiceException("患者档案已存在，请确认是否更新已有档案");
        } else {
            patientId = patientProfileManager.createArchive(profile, Collections.emptyList(), Collections.emptyList());
        }
        if (CollUtil.isNotEmpty(diseases)) {
            for (ChPatientDiseaseBo disease : diseases) {
                disease.setPatientId(patientId);
                patientProfileManager.bindDisease(disease);
            }
        }
        return patientId;
    }

    public List<Long> confirmMetrics(Long patientId, List<ChHealthMetricRecordBo> metrics) {
        if (CollUtil.isEmpty(metrics)) {
            return Collections.emptyList();
        }
        if (patientId == null) {
            patientId = metrics.get(0).getPatientId();
        }
        if (patientId == null) {
            throw new ServiceException("指标确认时患者ID不能为空");
        }
        for (ChHealthMetricRecordBo metric : metrics) {
            metric.setPatientId(patientId);
            metric.setDataSource("OCR");
        }
        return healthMetricManager.reportAndCheckBatch(metrics);
    }

    public Long confirmReport(Long patientId, ChHealthExamBo exam, List<ChHealthExamItemBo> items) {
        if (exam == null) {
            return null;
        }
        if (patientId == null) {
            patientId = exam.getPatientId();
        }
        if (patientId == null) {
            throw new ServiceException("报告确认时患者ID不能为空");
        }
        exam.setPatientId(patientId);
        List<ChHealthExamItemBo> safeItems = items == null ? new ArrayList<>() : items;
        return healthExamManager.syncLisWithItems(exam, safeItems);
    }

    private boolean shouldConfirm(String target, String value) {
        return "MIXED".equals(target) || value.equals(target);
    }
}
