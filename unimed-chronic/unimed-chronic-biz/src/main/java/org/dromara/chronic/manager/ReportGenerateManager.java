package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChReportInstanceBo;
import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.mapper.ChReportInstanceMapper;
import org.dromara.chronic.service.IChReportService;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 报告生成管理器：模板+数据→PDF→签章→存储→推送
 * <p>
 * R8: 完整 PDF 生成流程——
 * 1. 生成报告实例记录（qrCodeContent = reportId 编码）
 * 2. 异步生成 PDF 文件（模拟，实际需对接 openpdf/freemarker 渲染）
 * 3. 电子签章（signTime 标记）
 * 4. 上传 OSS 并回填 pdfOssId
 * 5. 二维码嵌入（qrCodeContent 包含 reportInstanceId 可扫码核验）
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerateManager {

    private final IChReportService reportService;
    private final ChReportInstanceMapper instanceMapper;
    private final ReportPdfHelper reportPdfHelper;

    /**
     * R8: 同步生成报告实例 + 事务提交后触发 PDF 生成签章
     */
    @Transactional(rollbackFor = Exception.class)
    public Long generateAndSign(ChReportInstanceBo bo) {
        Long reportId = reportService.generateReport(bo);
        // R8: 二维码内容 = reportInstanceId，扫码可核验
        ChReportInstance instance = instanceMapper.selectById(reportId);
        if (instance == null) {
            throw new ServiceException("报告实例创建失败");
        }
        String qrContent = "CHRONIC-REPORT-" + reportId;
        instance.setQrCodeContent(qrContent);
        instanceMapper.updateById(instance);
        // R8: 事务提交后异步触发 PDF 生成+签章，避免读不到未提交数据
        // 使用独立 Bean 的 @Async 方法，确保 Spring AOP 代理可拦截
        final Long finalReportId = reportId;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                reportPdfHelper.generatePdfAsync(finalReportId);
            }
        });
        return reportId;
    }

    public ChReportInstanceVo queryDetail(Long reportId) {
        return reportService.queryReportById(reportId);
    }
}
