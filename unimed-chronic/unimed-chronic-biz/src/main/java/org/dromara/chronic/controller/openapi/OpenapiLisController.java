package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.manager.HealthExamManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * LIS 同步开放接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-LIS同步")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiLisController {

    private final HealthExamManager healthExamManager;

    @Operation(summary = "LIS体检数据同步")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/lis/exam")
    public R<Long> syncExam(@Validated @RequestBody ChHealthExamBo bo,
                            @RequestBody(required = false) List<ChHealthExamItemBo> items) {
        return R.ok(healthExamManager.syncLisWithItems(bo, items));
    }
}
