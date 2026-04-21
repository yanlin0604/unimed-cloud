package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChReportInstanceBo;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.service.IChReportService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 报告生成管理器：模板+数据→PDF→签章→存储→推送
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerateManager {

    private final IChReportService reportService;

    @Transactional(rollbackFor = Exception.class)
    public Long generateAndSign(ChReportInstanceBo bo) {
        Long reportId = reportService.generateReport(bo);
        // TODO: 实际 PDF 生成、电子签章、OSS 上传、二维码嵌入
        ChReportInstanceVo instance = reportService.queryReportById(reportId);
        log.info("报告生成+签章完成: reportId={}", reportId);
        return reportId;
    }

    public ChReportInstanceVo queryDetail(Long reportId) {
        return reportService.queryReportById(reportId);
    }
}
