package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientTagDictBo;
import org.dromara.chronic.domain.vo.ChPatientTagDictVo;
import org.dromara.chronic.service.IChPatientTagDictService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 后台患者标签字典管理（对应 §2.15 患者标签管理页）。
 *
 * 标签字典是患者标签的权威来源；患者-标签的绑定关系仍由
 * {@link PatientTagController} 维护（ch_patient_tag.tag_code 引用本字典）。
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者标签字典")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/patient-tag-dict")
public class PatientTagDictController extends BaseController {

    private final IChPatientTagDictService patientTagDictService;

    @Operation(summary = "分页查询")
    @SaCheckPermission("chronic:patientTagDict:list")
    @GetMapping("/page")
    public TableDataInfo<ChPatientTagDictVo> page(ChPatientTagDictBo bo, PageQuery pageQuery) {
        return patientTagDictService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "列表查询（下拉选择用）")
    @SaCheckPermission("chronic:patientTagDict:list")
    @GetMapping("/list")
    public R<List<ChPatientTagDictVo>> list(ChPatientTagDictBo bo) {
        return R.ok(patientTagDictService.queryList(bo));
    }

    @Operation(summary = "字典详情")
    @SaCheckPermission("chronic:patientTagDict:query")
    @GetMapping("/{id}")
    public R<ChPatientTagDictVo> detail(@Parameter(description = "字典ID") @PathVariable Long id) {
        return R.ok(patientTagDictService.queryById(id));
    }

    @Operation(summary = "新增标签字典")
    @SaCheckPermission("chronic:patientTagDict:add")
    @Log(title = "患者标签字典", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody ChPatientTagDictBo bo) {
        return patientTagDictService.insertByBo(bo) ? R.ok() : R.fail();
    }

    @Operation(summary = "修改标签字典")
    @SaCheckPermission("chronic:patientTagDict:edit")
    @Log(title = "患者标签字典", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{id}")
    public R<Void> edit(@Parameter(description = "字典ID") @PathVariable Long id,
                        @Validated @RequestBody ChPatientTagDictBo bo) {
        bo.setId(id);
        return patientTagDictService.updateByBo(bo) ? R.ok() : R.fail();
    }

    @Operation(summary = "切换状态（启用/停用）")
    @SaCheckPermission("chronic:patientTagDict:edit")
    @Log(title = "患者标签字典", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/status")
    public R<Void> changeStatus(@Parameter(description = "字典ID") @PathVariable Long id,
                                @Parameter(description = "0启用 1停用") @RequestParam String status) {
        return patientTagDictService.changeStatus(id, status) ? R.ok() : R.fail();
    }

    @Operation(summary = "批量删除（被引用时禁止删除）")
    @SaCheckPermission("chronic:patientTagDict:remove")
    @Log(title = "患者标签字典", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@Parameter(description = "字典ID集合") @PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("ids 不能为空");
        }
        return patientTagDictService.deleteByIds(Arrays.asList(ids)) ? R.ok() : R.fail();
    }
}
