package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMedicalExamBo;
import org.dromara.chronic.domain.vo.ChMedicalExamVo;
import org.dromara.chronic.service.IChMedicalExamService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * 检查记录管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-检查记录")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/medical-exam")
public class MedicalExamController extends BaseController {

    private final IChMedicalExamService medicalExamService;

    @Operation(summary = "分页查询检查记录")
    @SaCheckPermission("chronic:medical-exam:list")
    @GetMapping("/page")
    public TableDataInfo<ChMedicalExamVo> page(ChMedicalExamBo bo, PageQuery pageQuery) {
        return medicalExamService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "查询检查详情")
    @SaCheckPermission("chronic:medical-exam:query")
    @GetMapping("/{examId}")
    public R<ChMedicalExamVo> detail(@Parameter(description = "检查ID") @PathVariable Long examId) {
        return R.ok(medicalExamService.queryById(examId));
    }

    @Operation(summary = "新增检查记录")
    @SaCheckPermission("chronic:medical-exam:add")
    @Log(title = "检查记录", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Long> add(@Validated @RequestBody ChMedicalExamBo bo) {
        return R.ok(medicalExamService.create(bo));
    }

    @Operation(summary = "更新检查记录")
    @SaCheckPermission("chronic:medical-exam:edit")
    @Log(title = "检查记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{examId}")
    public R<Void> edit(@Parameter(description = "检查ID") @PathVariable Long examId, @Validated @RequestBody ChMedicalExamBo bo) {
        bo.setExamId(examId);
        return toAjax(medicalExamService.update(bo));
    }

    @Operation(summary = "删除检查记录")
    @SaCheckPermission("chronic:medical-exam:remove")
    @Log(title = "检查记录", businessType = BusinessType.DELETE)
    @DeleteMapping(value = {"/{examIds}", ""})
    public R<Void> remove(@Parameter(description = "检查ID数组") @PathVariable(value = "examIds", required = false) Collection<Long> examIds,
                          @RequestBody(required = false) Collection<Long> bodyIds) {
        Collection<Long> ids = examIds != null ? examIds : bodyIds;
        if (ids == null || ids.isEmpty()) {
            return R.fail("缺少待删除ID");
        }
        return toAjax(medicalExamService.deleteByIds(ids));
    }
}
