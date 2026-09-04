package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChStandardDrug;
import org.dromara.chronic.mapper.ChStandardDrugMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 国家标准药品库与医保目录维护控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-国家标准药品库")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/drug")
public class StandardDrugController extends BaseController {

    private final ChStandardDrugMapper drugMapper;

    /**
     * 分页查询国家标准药品库
     */
    @Operation(summary = "分页查询标准药品")
    @SaCheckPermission("chronic:drug:query")
    @GetMapping("/page")
    public TableDataInfo<ChStandardDrug> page(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String chronicCategory,
                                              @RequestParam(required = false) String medicareCategory,
                                              PageQuery pageQuery) {
        Page<ChStandardDrug> result = drugMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChStandardDrug>lambdaQuery()
                .and(StrUtil.isNotBlank(keyword), w -> w.like(ChStandardDrug::getCommonName, keyword)
                    .or().like(ChStandardDrug::getTradeName, keyword)
                    .or().like(ChStandardDrug::getDrugCode, keyword)
                    .or().like(ChStandardDrug::getNationalCode, keyword))
                .eq(StrUtil.isNotBlank(chronicCategory), ChStandardDrug::getChronicCategory, chronicCategory)
                .eq(StrUtil.isNotBlank(medicareCategory), ChStandardDrug::getMedicareCategory, medicareCategory)
                .orderByAsc(ChStandardDrug::getDrugId)
        );
        return TableDataInfo.build(result);
    }

    /**
     * 新增或编辑标准药品
     */
    @Operation(summary = "新增或编辑标准药品")
    @SaCheckPermission("chronic:drug:edit")
    @RepeatSubmit
    @PostMapping("/save")
    public R<Long> save(@RequestBody ChStandardDrug drug) {
        if (StrUtil.isBlank(drug.getCommonName())) {
            throw new ServiceException("药品通用名称不能为空");
        }
        if (drug.getDrugId() == null) {
            drug.setDrugId(IdUtil.getSnowflakeNextId());
            if (StrUtil.isBlank(drug.getStatus())) {
                drug.setStatus("ENABLED");
            }
            drugMapper.insert(drug);
        } else {
            drugMapper.updateById(drug);
        }
        return R.ok(drug.getDrugId());
    }

    /**
     * 启用/停用药品
     */
    @Operation(summary = "切换药品启用状态")
    @SaCheckPermission("chronic:drug:edit")
    @PostMapping("/toggle-status/{drugId}")
    public R<Void> toggleStatus(@Parameter(description = "药品ID") @PathVariable Long drugId) {
        ChStandardDrug drug = drugMapper.selectById(drugId);
        if (drug == null) {
            throw new ServiceException("药品不存在");
        }
        drug.setStatus("ENABLED".equals(drug.getStatus()) ? "DISABLED" : "ENABLED");
        drugMapper.updateById(drug);
        return R.ok();
    }
}
