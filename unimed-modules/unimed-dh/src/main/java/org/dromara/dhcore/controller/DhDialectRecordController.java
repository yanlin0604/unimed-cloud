package org.dromara.dhcore.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhDialectRecordAuditBo;
import org.dromara.dhcore.domain.bo.DhDialectRecordQueryBo;
import org.dromara.dhcore.domain.vo.DhDialectRecordVo;
import org.dromara.dhcore.service.IDhDialectRecordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B端采集记录管理控制器
 *
 * @author unimed
 */
@Tag(name = "B端-采集记录管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/dialectRecord")
public class DhDialectRecordController extends BaseController {

    private final IDhDialectRecordService dialectRecordService;

    /**
     * 分页查询录音记录列表
     */
    @Operation(summary = "查询录音记录列表")
    @SaCheckPermission("dh:dialectRecord:list")
    @GetMapping("/list")
    public TableDataInfo<DhDialectRecordVo> list(DhDialectRecordQueryBo bo, PageQuery pageQuery) {
        return dialectRecordService.queryPage(bo, pageQuery);
    }

    /**
     * 审核录音记录
     */
    @Operation(summary = "审核录音记录")
    @SaCheckPermission("dh:dialectRecord:audit")
    @PutMapping("/audit")
    public R<Boolean> audit(@Validated @RequestBody DhDialectRecordAuditBo bo) {
        return R.ok(dialectRecordService.audit(bo));
    }

    /**
     * 删除录音记录
     */
    @Operation(summary = "删除录音记录")
    @SaCheckPermission("dh:dialectRecord:remove")
    @DeleteMapping("/{recordIds}")
    public R<Boolean> remove(@PathVariable List<Long> recordIds) {
        return R.ok(dialectRecordService.delete(recordIds));
    }

    /**
     * 导出录音记录
     */
    @Operation(summary = "导出录音记录")
    @SaCheckPermission("dh:dialectRecord:export")
    @PostMapping("/export")
    public void export(DhDialectRecordQueryBo bo, HttpServletResponse response) {
        dialectRecordService.export(bo, response);
    }
}
