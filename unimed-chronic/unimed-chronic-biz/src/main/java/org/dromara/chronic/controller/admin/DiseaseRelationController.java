package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDiseaseRelationBo;
import org.dromara.chronic.domain.entity.ChDiseaseRelation;
import org.dromara.chronic.domain.vo.ChDiseaseRelationVo;
import org.dromara.chronic.mapper.ChDiseaseRelationMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 病种关系控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-病种关系")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/disease-relation")
public class DiseaseRelationController {

    private final ChDiseaseRelationMapper diseaseRelationMapper;

    @Operation(summary = "查询病种关系列表")
    @SaCheckPermission("chronic:disease-relation:list")
    @GetMapping("/page")
    public R<List<ChDiseaseRelationVo>> page() {
        return R.ok(diseaseRelationMapper.selectVoList(Wrappers.lambdaQuery()));
    }

    @Operation(summary = "新增病种关系")
    @SaCheckPermission("chronic:disease-relation:add")
    @Log(title = "病种关系", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody ChDiseaseRelationBo bo) {
        ChDiseaseRelation entity = MapstructUtils.convert(bo, ChDiseaseRelation.class);
        return diseaseRelationMapper.insert(entity) > 0 ? R.ok() : R.fail();
    }
}
