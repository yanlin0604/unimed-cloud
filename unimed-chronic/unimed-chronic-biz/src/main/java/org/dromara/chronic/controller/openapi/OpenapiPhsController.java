package org.dromara.chronic.controller.openapi;

import org.dromara.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChExternalSyncLog;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.manager.PatientProfileManager;
import org.dromara.chronic.mapper.ChExternalSyncLogMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 基层卫生系统(PHS)档案交换开放接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-PHS档案交换")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiPhsController extends BaseController {

    private final PatientProfileManager patientProfileManager;
    private final ChExternalSyncLogMapper externalSyncLogMapper;

    /**
     * 基层档案同步：将基层卫生系统的患者档案同步到慢病管理
     */
    @Operation(summary = "基层档案同步")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/phs/archive/sync")
    public R<Long> archiveSync(@Validated @RequestBody ChPatientProfileBo bo,
                               @Parameter(description = "外部患者ID") @RequestParam(required = false) String externalPatientId) {
        Long patientId = patientProfileManager.createArchive(bo, java.util.Collections.emptyList(), java.util.Collections.emptyList());

        logSync("PHS_ARCHIVE", "INBOUND", "PHS", "SUCCESS",
            "档案同步: name=" + bo.getName() + ", externalPatientId=" + externalPatientId);

        return R.ok(patientId);
    }

    /**
     * 档案查询：基层系统查询慢病档案
     */
    @Operation(summary = "档案查询")
    @GetMapping("/chronic/openapi/phs/archive/{patientId}")
    public R<ChPatientDetailVo> archiveQuery(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(patientProfileManager.queryProfile(patientId));
    }

    private void logSync(String syncType, String direction, String system, String status, String detail) {
        ChExternalSyncLog syncLog = new ChExternalSyncLog();
        syncLog.setSyncType(syncType);
        syncLog.setSyncDirection(direction);
        syncLog.setExternalSystem(system);
        syncLog.setSyncStatus(status);
        syncLog.setSyncDetail(StringUtils.substring(detail, 0, 500));
        syncLog.setSyncTime(new Date());
        externalSyncLogMapper.insert(syncLog);
    }
}
