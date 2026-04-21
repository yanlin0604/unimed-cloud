package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChConsentRecordBo;
import org.dromara.chronic.domain.vo.ChConsentRecordVo;
import org.dromara.chronic.service.IChConsentRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知情同意管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-知情同意")
@Validated
@RestController
@RequiredArgsConstructor
public class ConsentController extends BaseController {

    private final IChConsentRecordService consentService;

    @SaCheckPermission("chronic:consent:list")
    @Operation(summary = "分页查询知情同意记录")
    @GetMapping("/chronic/admin/consent/page")
    public TableDataInfo<ChConsentRecordVo> page(ChConsentRecordBo bo, PageQuery pageQuery) {
        return consentService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("chronic:consent:query")
    @Operation(summary = "获取知情同意详情")
    @GetMapping("/chronic/admin/consent/{consentId}")
    public R<ChConsentRecordVo> detail(@Parameter(description = "知情同意ID") @PathVariable Long consentId) {
        return R.ok(consentService.queryById(consentId));
    }

    @SaCheckPermission("chronic:consent:add")
    @Operation(summary = "新增知情同意")
    @PostMapping("/chronic/admin/consent")
    public R<Void> add(@Validated @RequestBody ChConsentRecordBo bo) {
        return toAjax(consentService.insertByBo(bo) != null);
    }

    @SaCheckPermission("chronic:consent:edit")
    @Operation(summary = "修改知情同意")
    @PutMapping("/chronic/admin/consent")
    public R<Void> edit(@Validated @RequestBody ChConsentRecordBo bo) {
        return toAjax(consentService.updateByBo(bo));
    }

    @SaCheckPermission("chronic:consent:remove")
    @Operation(summary = "删除知情同意")
    @DeleteMapping("/chronic/admin/consent/{consentId}")
    public R<Void> remove(@Parameter(description = "知情同意ID") @PathVariable Long consentId) {
        return toAjax(consentService.deleteById(consentId));
    }

    @SaCheckPermission("chronic:consent:list")
    @Operation(summary = "获取患者知情同意列表")
    @GetMapping("/chronic/admin/consent/patient/{patientId}")
    public R<List<ChConsentRecordVo>> listByPatient(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(consentService.queryByPatientId(patientId));
    }
}
