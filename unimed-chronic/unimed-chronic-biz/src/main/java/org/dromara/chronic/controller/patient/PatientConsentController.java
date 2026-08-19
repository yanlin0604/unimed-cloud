package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChConsentRecordBo;
import org.dromara.chronic.domain.vo.ChConsentRecordVo;
import org.dromara.chronic.service.IChConsentRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 患者端知情同意
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端知情同意")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientConsentController extends BaseController {

    private final IChConsentRecordService consentRecordService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "签署知情同意")
    @Log(title = "知情同意签署", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/patient/consent/sign")
    public R<Long> sign(@RequestBody ChConsentRecordBo bo) {
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        return R.ok(consentRecordService.insertByBo(bo));
    }

    @Operation(summary = "查询签名记录分页")
    @GetMapping("/chronic/patient/consent/page")
    public TableDataInfo<ChConsentRecordVo> page(ChConsentRecordBo bo, PageQuery pageQuery) {
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        return consentRecordService.queryPageList(bo, pageQuery);
    }
}
