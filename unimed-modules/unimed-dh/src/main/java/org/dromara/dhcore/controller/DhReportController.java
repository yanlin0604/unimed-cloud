package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhPunishBo;
import org.dromara.dhcore.domain.bo.DhReportHandleBo;
import org.dromara.dhcore.domain.bo.DhReportQueryBo;
import org.dromara.dhcore.domain.vo.DhReportItemVo;
import org.dromara.dhcore.service.IDhReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人口播举报管理控制器
 */
@Tag(name = "数字人口�?举报管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/report")
public class DhReportController extends BaseController {

    private final IDhReportService dhReportService;

    @Operation(summary = "分页查询举报�?)
    @SaCheckPermission("dh:report:list")
    @GetMapping("/list")
    public TableDataInfo<DhReportItemVo> list(DhReportQueryBo bo, PageQuery pageQuery) {
        return dhReportService.queryReportPage(bo, pageQuery);
    }

    @Operation(summary = "处理举报")
    @SaCheckPermission("dh:report:handle")
    @PostMapping("/handle")
    public R<DhReportItemVo> handle(@Validated @RequestBody DhReportHandleBo bo) {
        return R.ok(dhReportService.handleReport(bo));
    }

    @Operation(summary = "处罚用户")
    @SaCheckPermission("dh:report:punish")
    @PostMapping("/punish")
    public R<Boolean> punish(@Validated @RequestBody DhPunishBo bo) {
        return R.ok(dhReportService.punishUser(bo));
    }
}
