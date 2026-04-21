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

    @Operation(summary = "新增签约服务包")
    @SaCheckPermission("chronic:contract-package:add")
    @Log(title = "签约服务包", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/package")
    public R<Void> addPackage(@Validated @RequestBody ChContractServicePackageBo bo) {
        return toAjax(patientContractService.createPackage(bo));
    }

    @Operation(summary = "查询履约记录")
    @SaCheckPermission("chronic:contract:query")
    @GetMapping("/{contractId}/fulfillment")
    public R<List<ChContractFulfillmentVo>> fulfillment(@Parameter(description = "签约ID") @PathVariable Long contractId) {
        return R.ok(patientContractService.queryFulfillmentList(contractId));
    }
}
