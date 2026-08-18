package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.domain.vo.PathwayProgressVo;
import org.dromara.chronic.manager.ContractHistoryManager;
import org.dromara.chronic.service.IChDoctorTeamService;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.chronic.service.IClinicalPathwayService;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;

/**
 * 医生端患者管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端患者")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/patient")
public class DoctorPatientController extends BaseController {

    private final IChPatientProfileService patientProfileService;
    private final IChPatientContractService patientContractService;
    private final IChDoctorTeamService doctorTeamService;
    private final ContractHistoryManager contractHistoryManager;
    private final IClinicalPathwayService clinicalPathwayService;

    /**
     * 我的患者列表
     */
    @Operation(summary = "我的患者分页")
    @SaCheckPermission("chronic:doctor:patient:list")
    @GetMapping("/page")
    public TableDataInfo<ChPatientProfileVo> page(ChPatientProfileBo bo, PageQuery pageQuery) {
        // 责任医生取自登录上下文，禁止前端传入，避免越权查看他人患者
        bo.setDoctorUserId(LoginHelper.getUserId());
        return patientProfileService.queryPageList(bo, pageQuery);
    }

    /**
     * 患者详情
     */
    @Operation(summary = "患者详情")
    @SaCheckPermission("chronic:doctor:patient:query")
    @GetMapping("/{patientId}")
    public R<ChPatientDetailVo> detail(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(patientProfileService.queryDetailById(patientId));
    }

    /**
     * 患者签约
     */
    @Operation(summary = "患者签约")
    @SaCheckPermission("chronic:doctor:patient:sign")
    @Log(title = "患者签约", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/{patientId}/sign")
    public R<Long> sign(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId, @Validated @RequestBody ChPatientContractBo bo) {
        bo.setPatientId(patientId);
        return R.ok(patientContractService.signContract(bo));
    }

    /**
     * 绑定团队
     * 具体团队绑定能力在任务8完成，这里先固定接口路径
     */
    @Operation(summary = "绑定团队")
    @SaCheckPermission("chronic:doctor:patient:bind-team")
    @PostMapping("/{patientId}/bind-team")
    public R<Void> bindTeam(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId, @RequestBody(required = false) Map<String, Object> body) {
        Long teamId = body == null || body.get("teamId") == null ? null : Long.valueOf(String.valueOf(body.get("teamId")));
        if (teamId == null) {
            return R.fail("teamId不能为空");
        }
        return toAjax(doctorTeamService.bindPatientTeam(patientId, teamId));
    }

    @Operation(summary = "签约历史")
    @SaCheckPermission("chronic:doctor:patient:query")
    @GetMapping("/{patientId}/contract/history")
    public R<List<ChPatientTimelineVo>> contractHistory(
            @Parameter(description = "患者ID", required = true) @PathVariable Long patientId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return R.ok(contractHistoryManager.queryContractHistory(patientId, eventType, limit));
    }

    /**
     * 获取患者的临床路径进度聚合数据
     */
    @Operation(summary = "管理路径进度")
    @SaCheckPermission("chronic:doctor:patient:pathway")
    @GetMapping("/{patientId}/pathway-progress")
    public R<PathwayProgressVo> getPathwayProgress(
            @Parameter(description = "患者ID", required = true) @PathVariable Long patientId,
            @Parameter(description = "病种编码") @RequestParam(required = false) String diseaseCode) {
        return R.ok(clinicalPathwayService.getPathwayProgress(patientId, diseaseCode));
    }
}
