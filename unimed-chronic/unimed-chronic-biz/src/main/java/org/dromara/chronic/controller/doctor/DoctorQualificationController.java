package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChDoctorQualification;
import org.dromara.chronic.mapper.ChDoctorQualificationMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端执业资质认证
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端执业资质")
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class DoctorQualificationController extends BaseController {

    private final ChDoctorQualificationMapper qualificationMapper;

    /**
     * 提交执业资质认证
     */
    @Operation(summary = "提交执业资质认证")
    @RepeatSubmit
    @PostMapping("/chronic/doctor/qualification/apply")
    public R<Long> apply(@RequestBody ChDoctorQualification entity) {
        Long doctorUserId = LoginHelper.getUserId();
        if (StrUtil.isBlank(entity.getCertificateNo()) || StrUtil.isBlank(entity.getOrgName())) {
            throw new ServiceException("执业机构与医师资格证书编码不能为空");
        }
        ChDoctorQualification existing = qualificationMapper.selectOne(
            Wrappers.<ChDoctorQualification>lambdaQuery()
                .eq(ChDoctorQualification::getDoctorUserId, doctorUserId)
                .last("limit 1")
        );

        entity.setDoctorUserId(doctorUserId);
        entity.setAuditStatus("PENDING");

        if (existing != null) {
            entity.setId(existing.getId());
            qualificationMapper.updateById(entity);
            return R.ok(existing.getId());
        } else {
            entity.setId(IdUtil.getSnowflakeNextId());
            qualificationMapper.insert(entity);
            return R.ok(entity.getId());
        }
    }

    /**
     * 查询我的执业资质审核状态
     */
    @Operation(summary = "查询我的执业资质审核状态")
    @GetMapping("/chronic/doctor/qualification/status")
    public R<ChDoctorQualification> myStatus() {
        Long doctorUserId = LoginHelper.getUserId();
        return R.ok(qualificationMapper.selectOne(
            Wrappers.<ChDoctorQualification>lambdaQuery()
                .eq(ChDoctorQualification::getDoctorUserId, doctorUserId)
                .last("limit 1")
        ));
    }
}
