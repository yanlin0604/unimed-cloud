package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChLifestyleRecordBo;
import org.dromara.chronic.domain.vo.ChLifestyleRecordVo;
import org.dromara.chronic.service.IChLifestyleRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 生活方式管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-生活方式")
@Validated
@RestController
@RequiredArgsConstructor
public class LifestyleController {

    private final IChLifestyleRecordService lifestyleRecordService;

    @Operation(summary = "新增生活方式记录")
    @SaCheckPermission("chronic:lifestyle:add")
    @Log(title = "生活方式记录", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/lifestyle")
    public R<Long> add(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChLifestyleRecordBo bo) {
        bo.setPatientId(patientId);
        return R.ok(lifestyleRecordService.add(bo));
    }

    @Operation(summary = "修改生活方式记录")
    @SaCheckPermission("chronic:lifestyle:edit")
    @Log(title = "生活方式记录", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/lifestyle/{id}")
    public R<Void> edit(@Parameter(description = "记录ID") @PathVariable Long id, @Validated @RequestBody ChLifestyleRecordBo bo) {
        bo.setId(id);
        return R.ok(lifestyleRecordService.update(bo));
    }

    @Operation(summary = "删除生活方式记录")
    @SaCheckPermission("chronic:lifestyle:remove")
    @Log(title = "生活方式记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/chronic/admin/lifestyle/{id}")
    public R<Void> remove(@Parameter(description = "记录ID") @PathVariable Long id) {
        return R.ok(lifestyleRecordService.remove(id));
    }

    @Operation(summary = "分页查询生活方式")
    @SaCheckPermission("chronic:lifestyle:list")
    @GetMapping("/chronic/admin/lifestyle/page")
    public TableDataInfo<ChLifestyleRecordVo> page(ChLifestyleRecordBo bo, PageQuery pageQuery) {
        return lifestyleRecordService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "生活方式记录详情")
    @SaCheckPermission("chronic:lifestyle:query")
    @GetMapping("/chronic/admin/lifestyle/{id}")
    public R<ChLifestyleRecordVo> detail(@Parameter(description = "记录ID") @PathVariable Long id) {
        return R.ok(lifestyleRecordService.queryById(id));
    }

    @Operation(summary = "查询生活方式趋势")
    @SaCheckPermission("chronic:lifestyle:query")
    @GetMapping("/chronic/admin/patient/{patientId}/lifestyle/trend")
    public R<List<ChLifestyleRecordVo>> trend(@Parameter(description = "患者ID") @PathVariable Long patientId,
                                               @Parameter(description = "查询最近记录数") @RequestParam(required = false, defaultValue = "12") Integer limit) {
        return R.ok(lifestyleRecordService.queryTrend(patientId, limit));
    }
}
