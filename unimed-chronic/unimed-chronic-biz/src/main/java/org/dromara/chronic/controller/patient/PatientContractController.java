package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChContractServicePackageBo;
import org.dromara.chronic.domain.bo.ChDoctorTeamBo;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChContractServicePackageVo;
import org.dromara.chronic.domain.vo.ChDoctorTeamVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.service.IChDoctorTeamService;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端签约管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端签约")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientContractController extends BaseController {

    private final IChPatientContractService contractService;
    private final IChDoctorTeamService doctorTeamService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询当前签约关系")
    @GetMapping("/chronic/patient/contract/current")
    public R<ChPatientContractVo> currentContract() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(contractService.queryCurrentContract(patientId));
    }

    @Operation(summary = "查询可用服务包列表")
    @GetMapping("/chronic/patient/contract/package/list")
    public TableDataInfo<ChContractServicePackageVo> packageList(ChContractServicePackageBo bo, PageQuery pageQuery) {
        bo.setIsActive(Boolean.TRUE);
        return contractService.queryPackagePageList(bo, pageQuery);
    }

    @Operation(summary = "查询可选医生团队列表")
    @GetMapping("/chronic/patient/contract/team/list")
    public TableDataInfo<ChDoctorTeamVo> teamList(ChDoctorTeamBo bo, PageQuery pageQuery) {
        return doctorTeamService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "患者发起签约")
    @Log(title = "患者签约", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/patient/contract/sign")
    public R<Long> signContract(@Validated @RequestBody ChPatientContractBo bo) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        bo.setPatientId(patientId);
        return R.ok(contractService.signContract(bo));
    }

    @Operation(summary = "查询履约明细分页")
    @GetMapping("/chronic/patient/contract/fulfillment/page")
    public TableDataInfo<ChContractFulfillmentVo> fulfillmentPage(PageQuery pageQuery) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChPatientContractVo contract = contractService.queryCurrentContract(patientId);
        if (contract == null) {
            return TableDataInfo.build();
        }
        return contractService.queryFulfillmentPage(contract.getContractId(), pageQuery);
    }
}
