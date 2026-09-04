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
import org.dromara.chronic.domain.entity.ChTreatmentRecord;
import org.dromara.chronic.mapper.ChTreatmentRecordMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 非药物治疗与康复记录管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-非药物治疗与康复")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/treatment")
public class TreatmentRecordController extends BaseController {

    private final ChTreatmentRecordMapper treatmentRecordMapper;

    @Operation(summary = "分页查询非药物治疗列表")
    @SaCheckPermission("chronic:treatment:query")
    @GetMapping("/page")
    public TableDataInfo<ChTreatmentRecord> page(@RequestParam(required = false) Long patientId,
                                                @RequestParam(required = false) String treatmentType,
                                                @RequestParam(required = false) String keyword,
                                                PageQuery pageQuery) {
        Page<ChTreatmentRecord> result = treatmentRecordMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChTreatmentRecord>lambdaQuery()
                .eq(patientId != null, ChTreatmentRecord::getPatientId, patientId)
                .eq(StrUtil.isNotBlank(treatmentType), ChTreatmentRecord::getTreatmentType, treatmentType)
                .and(StrUtil.isNotBlank(keyword), w -> w.like(ChTreatmentRecord::getTreatmentName, keyword)
                    .or().like(ChTreatmentRecord::getOperatorDoctorName, keyword))
                .orderByDesc(ChTreatmentRecord::getStartDate)
        );
        return TableDataInfo.build(result);
    }

    @Operation(summary = "新增或更新治疗记录")
    @SaCheckPermission("chronic:treatment:edit")
    @RepeatSubmit
    @PostMapping("/save")
    public R<Long> save(@RequestBody ChTreatmentRecord record) {
        if (record.getPatientId() == null) {
            throw new ServiceException("患者ID不能为空");
        }
        if (record.getTreatmentId() == null) {
            record.setTreatmentId(IdUtil.getSnowflakeNextId());
            treatmentRecordMapper.insert(record);
        } else {
            treatmentRecordMapper.updateById(record);
        }
        return R.ok(record.getTreatmentId());
    }

    @Operation(summary = "获取治疗记录详情")
    @SaCheckPermission("chronic:treatment:query")
    @GetMapping("/{id}")
    public R<ChTreatmentRecord> getDetail(@Parameter(description = "记录ID") @PathVariable Long id) {
        return R.ok(treatmentRecordMapper.selectById(id));
    }
}
