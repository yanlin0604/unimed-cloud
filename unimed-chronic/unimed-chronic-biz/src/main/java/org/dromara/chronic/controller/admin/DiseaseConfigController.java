package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDiseaseConfigBo;
import org.dromara.chronic.domain.vo.ChDiseaseConfigVo;
import org.dromara.chronic.service.IChDiseaseConfigService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 病种配置控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-病种配置")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/disease-config")
public class DiseaseConfigController extends BaseController {

    private final IChDiseaseConfigService diseaseConfigService;

    @Operation(summary = "分页查询病种配置")
    @SaCheckPermission("chronic:disease-config:list")
    @GetMapping("/page")
    public TableDataInfo<ChDiseaseConfigVo> page(ChDiseaseConfigBo bo, PageQuery pageQuery) {
        return diseaseConfigService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "病种配置详情")
    @SaCheckPermission("chronic:disease-config:query")
    @GetMapping("/{configId}")
    public R<ChDiseaseConfigVo> detail(@Parameter(description = "配置ID") @PathVariable Long configId) {
        return R.ok(diseaseConfigService.queryById(configId));
    }

    @Operation(summary = "新增病种配置")
    @SaCheckPermission("chronic:disease-config:add")
    @Log(title = "病种配置", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody ChDiseaseConfigBo bo) {
        return toAjax(diseaseConfigService.insertByBo(bo));
    }

    @Operation(summary = "修改病种配置")
    @SaCheckPermission("chronic:disease-config:edit")
    @Log(title = "病种配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{configId}")
    public R<Void> edit(@Parameter(description = "配置ID") @PathVariable Long configId, @Validated @RequestBody ChDiseaseConfigBo bo) {
        bo.setConfigId(configId);
        return toAjax(diseaseConfigService.updateByBo(bo));
    }

    @Operation(summary = "停用病种配置")
    @SaCheckPermission("chronic:disease-config:disable")
    @Log(title = "病种配置", businessType = BusinessType.UPDATE)
    @PostMapping("/{configId}/disable")
    public R<Void> disable(@Parameter(description = "配置ID") @PathVariable Long configId) {
        return toAjax(diseaseConfigService.disableById(configId));
    }
}
