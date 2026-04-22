package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.chronic.mapper.ChReportInstanceMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * 报告 PDF 异步生成辅助 Bean
 * <p>
 * 独立 Bean 确保 Spring AOP 代理可拦截 @Async 注解，
 * 避免 ReportGenerateManager 自调用导致 @Async 失效的问题。
 * <p>
 * 由 ReportGenerateManager 在事务提交后（TransactionSynchronization.afterCommit）
 * 调用，实现真正的异步 PDF 生成 + 电子签章 + OSS 上传。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportPdfHelper {

    private final ChReportInstanceMapper instanceMapper;

    /**
     * R8: 异步生成 PDF 文件 + 电子签章 + OSS 上传
     * <p>
     * 当前为模拟实现，实际需对接:
     * - openpdf / freemarker 渲染模板
     * - 电子签章服务
     * - RemoteOssService 上传
     * - ZXing 生成二维码嵌入 PDF
     *
     * @param reportId 报告实例ID
     */
    @Async("chronicAsyncExecutor")
    public void generatePdfAsync(Long reportId) {
        ChReportInstance instance = instanceMapper.selectById(reportId);
        if (instance == null) {
            log.warn("PDF生成: 报告实例不存在 reportId={}", reportId);
            return;
        }
        try {
            // 模拟 PDF 生成 + 签章
            String pdfOssId = "PDF-" + reportId + "-" + UUID.randomUUID().toString().substring(0, 8);
            instance.setPdfOssId(pdfOssId);
            instance.setSignTime(new Date());
            instanceMapper.updateById(instance);
            log.info("R8: 报告PDF生成+签章完成 reportId={} pdfOssId={}", reportId, pdfOssId);
        } catch (Exception e) {
            // R8: PDF 生成失败时标记推送状态为 FAILED，便于 UI/API 可见
            log.error("R8: 报告PDF生成失败 reportId={}", reportId, e);
            try {
                instance.setPushStatus("FAILED");
                instanceMapper.updateById(instance);
            } catch (Exception ex) {
                log.error("R8: 标记报告失败状态异常 reportId={}", reportId, ex);
            }
        }
    }
}
