package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientTagBo;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.vo.ChPatientTagVo;
import org.dromara.chronic.mapper.ChPatientTagMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 后台患者标签管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者标签")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/patient-tag")
public class PatientTagController extends BaseController {

    private final ChPatientTagMapper patientTagMapper;

    @Operation(summary = "分页查询患者标签")
    @SaCheckPermission("chronic:patientTag:list")
    @GetMapping("/page")
    public TableDataInfo<ChPatientTagVo> page(ChPatientTagBo bo, PageQuery pageQuery) {
        Page<ChPatientTagVo> result = patientTagMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(result);
    }

    @Operation(summary = "新增患者标签")
    @SaCheckPermission("chronic:patientTag:add")
    @Log(title = "患者标签", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody ChPatientTagBo bo) {
        ChPatientTag entity = BeanUtil.copyProperties(bo, ChPatientTag.class);
        return patientTagMapper.insert(entity) > 0 ? R.ok() : R.fail();
    }

    @Operation(summary = "批量打标签")
    @SaCheckPermission("chronic:patientTag:add")
    @Log(title = "批量打标签", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/batch")
    public R<Void> batchAdd(@Parameter(description = "患者ID列表") @RequestParam Long[] patientIds,
                            @Parameter(description = "标签类型") @RequestParam String tagType,
                            @Parameter(description = "标签值") @RequestParam String tagValue) {
        if (patientIds == null || patientIds.length == 0) {
            return R.fail("患者ID列表不能为空");
        }
        List<ChPatientTag> entities = new ArrayList<>();
        for (Long patientId : patientIds) {
            ChPatientTag entity = new ChPatientTag();
            entity.setPatientId(patientId);
            entity.setTagType(tagType);
            entity.setTagValue(tagValue);
            entities.add(entity);
        }
        return patientTagMapper.insertBatch(entities) ? R.ok() : R.fail();
    }

    @Operation(summary = "修改患者标签")
    @SaCheckPermission("chronic:patientTag:edit")
    @Log(title = "患者标签", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/{id}")
    public R<Void> edit(@Parameter(description = "标签ID") @PathVariable Long id,
                        @Validated @RequestBody ChPatientTagBo bo) {
        bo.setId(id);
        ChPatientTag entity = BeanUtil.copyProperties(bo, ChPatientTag.class);
        return patientTagMapper.updateById(entity) > 0 ? R.ok() : R.fail();
    }

    @Operation(summary = "删除患者标签")
    @SaCheckPermission("chronic:patientTag:remove")
    @Log(title = "患者标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@Parameter(description = "标签ID集合") @PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.fail("ids 不能为空");
        }
        return patientTagMapper.deleteByIds(Arrays.asList(ids)) > 0 ? R.ok() : R.fail();
    }

    @Operation(summary = "标签详情")
    @SaCheckPermission("chronic:patientTag:query")
    @GetMapping("/{id}")
    public R<ChPatientTagVo> detail(@Parameter(description = "标签ID") @PathVariable Long id) {
        return R.ok(patientTagMapper.selectVoById(id));
    }

    private LambdaQueryWrapper<ChPatientTag> buildQueryWrapper(ChPatientTagBo bo) {
        LambdaQueryWrapper<ChPatientTag> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChPatientTag::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getTagType()), ChPatientTag::getTagType, bo.getTagType());
        lqw.like(StringUtils.isNotBlank(bo.getTagValue()), ChPatientTag::getTagValue, bo.getTagValue());
        lqw.orderByDesc(ChPatientTag::getCreateTime);
        return lqw;
    }
}
