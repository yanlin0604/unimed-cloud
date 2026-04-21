package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChIcdDictVo;
import org.dromara.chronic.service.IChDiseaseConfigService;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ICD 字典控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-ICD字典")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/icd-dict")
public class IcdDictController {

    private final IChDiseaseConfigService diseaseConfigService;

    @Operation(summary = "查询ICD字典")
    @SaCheckPermission("chronic:icd-dict:list")
    @GetMapping("/page")
    public R<List<ChIcdDictVo>> page(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        return R.ok(diseaseConfigService.queryIcdList(keyword));
    }
}
