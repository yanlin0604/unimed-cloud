package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.chronic.service.IChReferralService;
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
 * 患者端双向转诊查询控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端双向转诊")
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class PatientReferralController extends BaseController {

    private final IChReferralService referralService;
    private final PatientContextHelper patientContextHelper;

    /**
     * 查询个人转诊记录分页
     */
    @Operation(summary = "查询个人转诊记录分页")
    @GetMapping("/chronic/patient/referral/page")
    public TableDataInfo<ChReferralRecordVo> page(ChReferralRecordBo bo, PageQuery pageQuery) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        bo.setPatientId(patientId);
        return referralService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询个人转诊详情
     */
    @Operation(summary = "查询个人转诊详情")
    @GetMapping("/chronic/patient/referral/{referralId}")
    public R<ChReferralRecordVo> detail(@Parameter(description = "转诊ID") @PathVariable Long referralId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChReferralRecordVo vo = referralService.queryById(referralId);
        if (vo == null || !patientId.equals(vo.getPatientId())) {
            throw new ServiceException("转诊记录不存在或无权查看");
        }
        return R.ok(vo);
    }
}
