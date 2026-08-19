package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChContractServicePackageBo;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChContractServicePackageVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.manager.ContractHistoryManager;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 签约管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-签约管理")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/contract")
public class ContractController extends BaseController {

    private final IChPatientContractService patientContractService;
    private final ContractHistoryManager contractHistoryManager;

    @Operation(summary = "分页查询签约")
    @SaCheckPermission("chronic:contract:list")
    @GetMapping("/page")
    public TableDataInfo<ChPatientContractVo> page(ChPatientContractBo bo, PageQuery pageQuery) {
        return patientContractService.queryContractPageList(bo, pageQuery);
    }

    @Operation(summary = "分页查询服务包")
    @SaCheckPermission("chronic:contract-package:list")
    @GetMapping("/package/page")
    public TableDataInfo<ChContractServicePackageVo> packagePage(ChContractServicePackageBo bo, PageQuery pageQuery) {
        return patientContractService.queryPackagePageList(bo, pageQuery);
    }

    @Operation(summary = "查询服务包详情")
    @SaCheckPermission("chronic:contract-package:query")
    @GetMapping("/package/{packageId}")
    public R<ChContractServicePackageVo> packageDetail(@Parameter(description = "服务包ID", required = true) @PathVariable Long packageId) {
        return R.ok(patientContractService.queryPackageById(packageId));
    }

    @Operation(summary = "新增签约服务包")
    @SaCheckPermission("chronic:contract-package:add")
    @Log(title = "签约服务包", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/package")
    public R<Void> addPackage(@Validated @RequestBody ChContractServicePackageBo bo) {
        return toAjax(patientContractService.createPackage(bo));
    }

    @Operation(summary = "发起患者签约")
    @SaCheckPermission("chronic:contract:add")
    @Log(title = "患者签约", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Long> addContract(@Validated @RequestBody ChPatientContractBo bo) {
        return R.ok(patientContractService.signContract(bo));
    }

    @Operation(summary = "查询履约记录")
    @SaCheckPermission("chronic:contract:query")
    @GetMapping("/{contractId}/fulfillment")
    public R<List<ChContractFulfillmentVo>> fulfillment(@Parameter(description = "签约ID") @PathVariable Long contractId) {
        return R.ok(patientContractService.queryFulfillmentList(contractId));
    }

    @Operation(summary = "签约时间线")
    @SaCheckPermission("chronic:contract:query")
    @GetMapping("/contract/{contractId}/timeline")
    public TableDataInfo<ChPatientTimelineVo> timeline(@PathVariable Long contractId, PageQuery pageQuery) {
        ChPatientContractVo contract = patientContractService.queryById(contractId);
        return contractHistoryManager.queryContractTimeline(contract.getPatientId(), pageQuery);
    }

    @Operation(summary = "当前有效签约")
    @SaCheckPermission("chronic:contract:query")
    @GetMapping("/patient/{patientId}/contract/current")
    public R<ChPatientContractVo> currentContract(@PathVariable Long patientId) {
        return R.ok(patientContractService.queryCurrentContract(patientId));
    }

    @Operation(summary = "发送续约提醒")
    @SaCheckPermission("chronic:contract:remind")
    @Log(title = "续约提醒", businessType = BusinessType.UPDATE)
    @PostMapping("/contract/{contractId}/renewal-remind")
    public R<Void> renewalRemind(@PathVariable Long contractId) {
        return R.ok(contractHistoryManager.sendRenewalReminder(contractId));
    }
}
