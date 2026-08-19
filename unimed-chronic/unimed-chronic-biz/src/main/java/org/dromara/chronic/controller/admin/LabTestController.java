package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChLabTestBo;
import org.dromara.chronic.domain.vo.ChLabTestVo;
import org.dromara.chronic.service.IChLabTestService;
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
 * 检验记录管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-检验记录")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/lab-test")
public class LabTestController extends BaseController {

    private final IChLabTestService labTestService;

    @Operation(summary = "分页查询检验记录")
    @SaCheckPermission("chronic:lab-test:list")
    @GetMapping("/page")
    public TableDataInfo<ChLabTestVo> page(ChLabTestBo bo, PageQuery pageQuery) {
        return labTestService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "查询检验详情")
    @SaCheckPermission("chronic:lab-test:query")
    @GetMapping("/{testId}")
    public R<ChLabTestVo> detail(@Parameter(description = "检验ID") @PathVariable Long testId) {
        return R.ok(labTestService.queryById(testId));
    }

    @Operation(summary = "新增检验记录")
    @SaCheckPermission("chronic:lab-test:add")
    @Log(title = "检验记录", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Long> add(@Validated @RequestBody ChLabTestBo bo) {
        return R.ok(labTestService.create(bo));
    }

    @Operation(summary = "更新检验记录")
    @SaCheckPermission("chronic:lab-test:edit")
    @Log(title = "检验记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{testId}")
    public R<Void> edit(@Parameter(description = "检验ID") @PathVariable Long testId, @Validated @RequestBody ChLabTestBo bo) {
        bo.setTestId(testId);
        return toAjax(labTestService.update(bo));
    }

    @Operation(summary = "删除检验记录")
    @SaCheckPermission("chronic:lab-test:remove")
    @Log(title = "检验记录", businessType = BusinessType.DELETE)
    @DeleteMapping(value = {"/{testIds}", ""})
    public R<Void> remove(@Parameter(description = "检验ID数组") @PathVariable(value = "testIds", required = false) Collection<Long> testIds,
                          @RequestBody(required = false) Collection<Long> bodyIds) {
        Collection<Long> ids = testIds != null ? testIds : bodyIds;
        if (ids == null || ids.isEmpty()) {
            return R.fail("缺少待删除ID");
        }
        return toAjax(labTestService.deleteByIds(ids));
    }
}
