package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端家属代管接口
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-患者端家属代管")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientFamilyController extends BaseController {

    private final IChPatientAccountService patientAccountService;

    /**
     * 绑定家属代管
     * <p>
     * 主账号一律取登录态：原实现 masterAccountId 完全来自请求体、仅校验主账号存在，
     * 患者 A 可把自己注册成患者 B 的家属代管，从而合法获得 B 的档案访问权（提权）。
     */
    @Operation(summary = "绑定家属代管")
    @PostMapping("/chronic/patient/family/bind")
    public R<Boolean> bindFamily(@Validated @RequestBody ChPatientAccountBo bo) {
        Long loginAccountId = LoginHelper.getUserId();
        if (loginAccountId == null) {
            throw new ServiceException("未登录");
        }
        bo.setMasterAccountId(loginAccountId);
        return R.ok(patientAccountService.bindFamilyProxy(bo));
    }

    /**
     * 家属代管列表
     */
    @Operation(summary = "家属代管列表")
    @GetMapping("/chronic/patient/family/list")
    public R<List<ChPatientAccountVo>> listFamily() {
        // 此处 LoginHelper.getUserId() 语义就是 accountId，是正确用法
        Long masterAccountId = LoginHelper.getUserId();
        return R.ok(patientAccountService.queryFamilyProxies(masterAccountId));
    }

    /**
     * 解绑家属代管
     * <p>
     * 原实现只校验「账号存在」「是代管账号」，不校验 master 是否为登录账号，
     * 任意患者可枚举 accountId 解绑他人的家属代管。
     */
    @Operation(summary = "解绑家属代管")
    @DeleteMapping("/chronic/patient/family/{accountId}")
    public R<Boolean> unbindFamily(@Parameter(description = "账号ID") @PathVariable Long accountId) {
        Long loginAccountId = LoginHelper.getUserId();
        ChPatientAccountVo target = patientAccountService.getAccountById(accountId);
        if (target == null || loginAccountId == null
            || !loginAccountId.equals(target.getMasterAccountId())) {
            log.warn("patient-family-unbind-denied: loginAccountId={}, targetAccountId={}", loginAccountId, accountId);
            throw new ServiceException("账号不存在或无权操作");
        }
        return R.ok(patientAccountService.unbindFamilyProxy(accountId));
    }
}
