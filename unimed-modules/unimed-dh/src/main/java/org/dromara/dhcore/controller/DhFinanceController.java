package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhFinanceDetailQueryBo;
import org.dromara.dhcore.domain.bo.DhFinanceSummaryQueryBo;
import org.dromara.dhcore.domain.vo.DhFinanceSummaryVo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;
import org.dromara.dhcore.service.IDhFinanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人口播财务报表控制器
 */
@Tag(name = "数字人口�?财务报表")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/finance")
public class DhFinanceController extends BaseController {

    private final IDhFinanceService dhFinanceService;

    @Operation(summary = "查询财务汇�?)
    @SaCheckPermission("dh:finance:list")
    @GetMapping("/summary")
    public R<DhFinanceSummaryVo> summary(DhFinanceSummaryQueryBo bo) {
        return R.ok(dhFinanceService.querySummary(bo));
    }

    @Operation(summary = "分页查询财务明细")
    @SaCheckPermission("dh:finance:list")
    @GetMapping("/detail/list")
    public TableDataInfo<DhWalletLogVo> detailList(DhFinanceDetailQueryBo bo, PageQuery pageQuery) {
        return dhFinanceService.queryDetailPage(bo, pageQuery);
    }
}
