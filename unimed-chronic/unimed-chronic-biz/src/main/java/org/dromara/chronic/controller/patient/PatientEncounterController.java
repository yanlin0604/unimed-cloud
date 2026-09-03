package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.chronic.manager.EncounterManager;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端诊疗记录（门诊/住院时序）
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端诊疗记录")
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class PatientEncounterController extends BaseController {

    private final EncounterManager encounterManager;
    private final PatientContextHelper patientContextHelper;

    /**
     * 患者查询个人就诊记录分页
     */
    @Operation(summary = "查询个人就诊记录分页")
    @GetMapping("/chronic/patient/encounter/page")
    public TableDataInfo<ChEncounterRecordVo> page(ChEncounterRecordBo bo, PageQuery pageQuery) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        bo.setPatientId(patientId);
        // 患者端仅展示已提交的就诊记录
        bo.setSubmitStatus("SUBMITTED");
        return encounterManager.queryPageList(bo, pageQuery);
    }

    /**
     * 患者查询个人就诊记录详情
     */
    @Operation(summary = "查询个人就诊记录详情")
    @GetMapping("/chronic/patient/encounter/{encounterId}")
    public R<ChEncounterRecordVo> detail(@Parameter(description = "就诊记录ID") @PathVariable Long encounterId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChEncounterRecordVo vo = encounterManager.queryById(encounterId);
        if (vo == null || !patientId.equals(vo.getPatientId())) {
            throw new ServiceException("就诊记录不存在或无权查看");
        }
        return R.ok(vo);
    }
}
