package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhAuditLogQueryBo;
import org.dromara.dhcore.domain.vo.DhAuditLogVo;
import org.dromara.dhcore.service.IDhAuditService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人口播审计日志控制器
 */
@Tag(name = "数字人口播-审计日志")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/audit")
public class DhAuditController extends BaseController {

    private final IDhAuditService dhAuditService;

    @Operation(summary = "分页查询审计日志")
    @SaCheckPermission("dh:audit:list")
    @GetMapping("/list")
    public TableDataInfo<DhAuditLogVo> list(DhAuditLogQueryBo bo, PageQuery pageQuery) {
        return dhAuditService.queryAuditLogPage(bo, pageQuery);
    }
}
