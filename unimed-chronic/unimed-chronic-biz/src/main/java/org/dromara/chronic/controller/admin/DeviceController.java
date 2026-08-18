package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 物联设备管理后台
 * <p>
 * 管理 ch_device_bind 绑定关系与 ch_device_raw_record 原始上报数据。
 *
 * @author unimed
 */
@Tag(name = "慢病管理-设备管理")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/device")
public class DeviceController extends BaseController {

    private final IChDeviceBindService deviceBindService;

    @Operation(summary = "分页查询设备绑定")
    @SaCheckPermission("chronic:device:list")
    @GetMapping("/page")
    public TableDataInfo<ChDeviceBindVo> page(ChDeviceBindBo bo, PageQuery pageQuery) {
        return deviceBindService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "分页查询设备原始上报数据")
    @SaCheckPermission("chronic:device:query")
    @GetMapping("/raw-record/page")
    public TableDataInfo<ChDeviceRawRecordVo> rawRecordPage(
        @Parameter(description = "设备ID") @RequestParam(required = false) String deviceId,
        @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
        PageQuery pageQuery) {
        return deviceBindService.queryRawRecordPage(deviceId, patientId, pageQuery);
    }

    @Operation(summary = "设备绑定详情")
    @SaCheckPermission("chronic:device:query")
    @GetMapping("/{bindId}")
    public R<ChDeviceBindVo> detail(@Parameter(description = "绑定ID") @PathVariable Long bindId) {
        return R.ok(deviceBindService.queryById(bindId));
    }

    @Operation(summary = "绑定设备")
    @SaCheckPermission("chronic:device:add")
    @Log(title = "设备绑定", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/bind")
    public R<Long> bind(@Validated @RequestBody ChDeviceBindBo bo) {
        return R.ok(deviceBindService.bindDevice(bo));
    }

    @Operation(summary = "解绑设备")
    @SaCheckPermission("chronic:device:remove")
    @Log(title = "设备解绑", businessType = BusinessType.DELETE)
    @RepeatSubmit
    @DeleteMapping("/{bindId}")
    public R<Void> unbind(@Parameter(description = "绑定ID") @PathVariable Long bindId) {
        return R.ok(deviceBindService.unbindDevice(bindId));
    }
}
