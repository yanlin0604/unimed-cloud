package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询当前签约关系")
    @GetMapping("/chronic/patient/contract/current")
    public R<ChPatientContractVo> currentContract() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(contractService.queryCurrentContract(patientId));
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
