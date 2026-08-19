package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.service.IChHealthExamService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端体检检验
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端体检检验")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientHealthExamController extends BaseController {

    private final IChHealthExamService healthExamService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询体检检验列表")
    @GetMapping("/chronic/patient/health-exams")
    public R<List<ChHealthExamVo>> list() {
        // 原实现用 LoginHelper.getUserId()，那是 accountId 不是 patientId（线上 21001~21008 vs 1001~1010），
        // 查不到任何数据 → 列表永远为空；且一旦 patientId 增长到与 accountId 区间重叠即变成跨患者泄露。
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(healthExamService.queryByPatientId(patientId));
    }

    @Operation(summary = "体检检验详情")
    @GetMapping("/chronic/patient/health-exam/{examId}")
    public R<ChHealthExamVo> detail(@Parameter(description = "体检检验ID") @PathVariable Long examId) {
        // 原实现直接按 examId 返回，而 exam_id 是 36001~36008 连续整数，
        // 可枚举读取他人体检报告全文（服务层还会回填他人姓名）。
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChHealthExamVo exam = healthExamService.queryById(examId);
        if (exam == null || !patientId.equals(exam.getPatientId())) {
            throw new ServiceException("体检记录不存在或无权访问");
        }
        return R.ok(exam);
    }
}
