package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChPrescription;
import org.dromara.chronic.mapper.ChPrescriptionMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 结构化处方单管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-结构化处方单")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/prescription")
public class PrescriptionController extends BaseController {

    private final ChPrescriptionMapper prescriptionMapper;

    /**
     * 分页查询处方单列表
     */
    @Operation(summary = "分页查询处方列表")
    @SaCheckPermission("chronic:prescription:query")
    @GetMapping("/page")
    public TableDataInfo<ChPrescription> page(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String prescriptionType,
                                               PageQuery pageQuery) {
        Page<ChPrescription> result = prescriptionMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChPrescription>lambdaQuery()
                .and(StrUtil.isNotBlank(keyword), w -> w.like(ChPrescription::getPrescriptionNo, keyword)
                    .or().like(ChPrescription::getPatientName, keyword)
                    .or().like(ChPrescription::getDoctorName, keyword))
                .eq(StrUtil.isNotBlank(prescriptionType), ChPrescription::getPrescriptionType, prescriptionType)
                .orderByDesc(ChPrescription::getPrescriptionTime)
        );
        return TableDataInfo.build(result);
    }

    /**
     * 开具或更新结构化处方
     */
    @Operation(summary = "开具或更新处方单")
    @SaCheckPermission("chronic:prescription:edit")
    @RepeatSubmit
    @PostMapping("/save")
    public R<Long> save(@RequestBody ChPrescription prescription) {
        if (prescription.getPatientId() == null) {
            throw new ServiceException("就诊患者不能为空");
        }
        if (prescription.getPrescriptionId() == null) {
            prescription.setPrescriptionId(IdUtil.getSnowflakeNextId());
            prescription.setPrescriptionNo("RX" + System.currentTimeMillis() + (int)(Math.random() * 1000));
            prescription.setStatus("VALID");
            if (prescription.getPrescriptionTime() == null) {
                prescription.setPrescriptionTime(LocalDateTime.now());
            }
            prescriptionMapper.insert(prescription);
        } else {
            prescriptionMapper.updateById(prescription);
        }
        return R.ok(prescription.getPrescriptionId());
    }

    /**
     * 获取处方详情
     */
    @Operation(summary = "获取处方详情")
    @SaCheckPermission("chronic:prescription:query")
    @GetMapping("/{id}")
    public R<ChPrescription> getDetail(@Parameter(description = "处方ID") @PathVariable Long id) {
        return R.ok(prescriptionMapper.selectById(id));
    }
}
