package org.dromara.chronic.manager;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.service.IChHealthExamService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 体检检验管理器：LIS 同步幂等校验 + 专项评估项批量写入
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HealthExamManager {

    private final IChHealthExamService healthExamService;

    @Transactional(rollbackFor = Exception.class)
    public Long syncLisWithItems(ChHealthExamBo bo, List<ChHealthExamItemBo> items) {
        Long examId = healthExamService.syncLisExam(bo);
        if (CollUtil.isNotEmpty(items)) {
            for (ChHealthExamItemBo item : items) {
                item.setExamId(examId);
                healthExamService.addItem(item);
            }
        }
        return examId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long syncPacsWithItems(ChHealthExamBo bo, List<ChHealthExamItemBo> items) {
        Long examId = healthExamService.syncPacsExam(bo);
        if (CollUtil.isNotEmpty(items)) {
            for (ChHealthExamItemBo item : items) {
                item.setExamId(examId);
                healthExamService.addItem(item);
            }
        }
        return examId;
    }

    public ChHealthExamVo queryDetail(Long examId) {
        return healthExamService.queryById(examId);
    }
}
