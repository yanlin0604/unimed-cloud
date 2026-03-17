package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhTopupApproveBo;
import org.dromara.dhcore.domain.bo.DhTopupNeedMoreBo;
import org.dromara.dhcore.domain.bo.DhTopupQueryBo;
import org.dromara.dhcore.domain.bo.DhTopupRejectBo;
import org.dromara.dhcore.domain.vo.DhTopupTicketVo;
import org.dromara.dhcore.service.IDhTopupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人口播充值审核控制器
 */
@Tag(name = "数字人口�?充值审�?)
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/topup")
public class DhTopupController extends BaseController {

    private final IDhTopupService dhTopupService;

    @Operation(summary = "分页查询充值工�?)
    @SaCheckPermission("dh:topup:list")
    @GetMapping("/list")
    public TableDataInfo<DhTopupTicketVo> list(DhTopupQueryBo bo, PageQuery pageQuery) {
        return dhTopupService.queryTopupPage(bo, pageQuery);
    }

    @Operation(summary = "确认充值到�?)
    @SaCheckPermission("dh:topup:approve")
    @PostMapping("/approve")
    public R<DhTopupTicketVo> approve(@Validated @RequestBody DhTopupApproveBo bo) {
        return R.ok(dhTopupService.approveTopup(bo));
    }

    @Operation(summary = "标记工单需补充")
    @SaCheckPermission("dh:topup:needMore")
    @PostMapping("/needMore")
    public R<DhTopupTicketVo> needMore(@Validated @RequestBody DhTopupNeedMoreBo bo) {
        return R.ok(dhTopupService.markTopupNeedMore(bo));
    }

    @Operation(summary = "驳回充值工�?)
    @SaCheckPermission("dh:topup:reject")
    @PostMapping("/reject")
    public R<DhTopupTicketVo> reject(@Validated @RequestBody DhTopupRejectBo bo) {
        return R.ok(dhTopupService.rejectTopup(bo));
    }
}
