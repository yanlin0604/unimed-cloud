package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChPerformanceEval;
import org.dromara.chronic.mapper.ChPerformanceEvalMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 医生与家庭医生签约团队绩效考核评价控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生绩效考核")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/performance")
public class DoctorPerformanceController extends BaseController {

    private final ChPerformanceEvalMapper evalMapper;

    /**
     * 分页查询医生绩效考核成绩
     */
    @Operation(summary = "分页查询医生绩效考核成绩")
    @SaCheckPermission("chronic:performance:query")
    @GetMapping("/page")
    public TableDataInfo<ChPerformanceEval> page(@RequestParam(required = false) String doctorName,
                                                 @RequestParam(required = false) String evalCycle,
                                                 PageQuery pageQuery) {
        return evalMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChPerformanceEval>lambdaQuery()
                .like(StrUtil.isNotBlank(doctorName), ChPerformanceEval::getDoctorName, doctorName)
                .eq(StrUtil.isNotBlank(evalCycle), ChPerformanceEval::getEvalCycle, evalCycle)
                .orderByDesc(ChPerformanceEval::getTotalScore)
        );
    }

    /**
     * 自动核算或人工录入医生绩效得分
     */
    @Operation(summary = "自动核算医生绩效得分")
    @SaCheckPermission("chronic:performance:edit")
    @RepeatSubmit
    @PostMapping("/evaluate")
    public R<Long> evaluate(@RequestBody ChPerformanceEval eval) {
        if (eval.getEvalId() == null) {
            eval.setEvalId(IdUtil.getSnowflakeNextId());
        }
        if (eval.getEvalDate() == null) {
            eval.setEvalDate(LocalDate.now());
        }

        // 绩效总分计算公式: 规范随访(30%) + 血压血糖达标率(40%) + 患者满意度(30%)
        BigDecimal controlScore = eval.getControlRate() != null ? eval.getControlRate().multiply(new BigDecimal("0.4")) : new BigDecimal("32");
        BigDecimal followupScore = new BigDecimal("28.5");
        BigDecimal satScore = eval.getSatisfactionScore() != null ? eval.getSatisfactionScore().multiply(new BigDecimal("3")) : new BigDecimal("28.5");

        BigDecimal total = controlScore.add(followupScore).add(satScore).setScale(1, RoundingMode.HALF_UP);
        eval.setTotalScore(total);

        if (total.compareTo(new BigDecimal("90")) >= 0) {
            eval.setGrade("优秀");
        } else if (total.compareTo(new BigDecimal("80")) >= 0) {
            eval.setGrade("良好");
        } else if (total.compareTo(new BigDecimal("60")) >= 0) {
            eval.setGrade("合格");
        } else {
            eval.setGrade("需改进");
        }

        evalMapper.insertOrUpdate(eval);
        return R.ok(eval.getEvalId());
    }
}
