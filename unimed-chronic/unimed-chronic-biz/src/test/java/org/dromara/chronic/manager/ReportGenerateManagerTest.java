package org.dromara.chronic.manager;

import org.dromara.chronic.domain.bo.ChReportInstanceBo;
import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.chronic.mapper.ChReportInstanceMapper;
import org.dromara.chronic.service.IChReportService;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportGenerateManager 测试
 * 验证报告生成、QR码内容设置、事务提交后触发异步 PDF 生成
 * <p>
 * 注意：generateAndSign 内部调用 TransactionSynchronizationManager.registerSynchronization，
 * 单元测试无 Spring 事务上下文，因此需要 MockedStatic 来跳过该静态调用，
 * 并手动验证 afterCommit 回调是否触发了 reportPdfHelper.generatePdfAsync。
 *
 * @author unimed
 */
@ExtendWith(MockitoExtension.class)
class ReportGenerateManagerTest {

    @Mock
    private IChReportService reportService;

    @Mock
    private ChReportInstanceMapper instanceMapper;

    @Mock
    private ReportPdfHelper reportPdfHelper;

    @InjectMocks
    private ReportGenerateManager reportGenerateManager;

    @Test
    void shouldGenerateReportAndSetQrContent() {
        ChReportInstanceBo bo = new ChReportInstanceBo();
        bo.setPatientId(1L);
        bo.setTemplateId(100L);

        when(reportService.generateReport(bo)).thenReturn(1L);

        ChReportInstance instance = new ChReportInstance();
        instance.setReportId(1L);
        instance.setPatientId(1L);
        when(instanceMapper.selectById(1L)).thenReturn(instance);
        when(instanceMapper.updateById(any(ChReportInstance.class))).thenReturn(1);

        // MockedStatic: 跳过 TransactionSynchronizationManager 静态调用（无事务上下文）
        try (MockedStatic<TransactionSynchronizationManager> tsmMock =
                 mockStatic(TransactionSynchronizationManager.class)) {
            tsmMock.when(() -> TransactionSynchronizationManager.registerSynchronization(any()))
                .thenAnswer(invocation -> {
                    // 捕获 afterCommit 回调并直接触发，验证异步 PDF 生成调用链
                    TransactionSynchronization sync = invocation.getArgument(0);
                    sync.afterCommit();
                    return null;
                });
            tsmMock.when(TransactionSynchronizationManager::isSynchronizationActive).thenReturn(true);

            Long reportId = reportGenerateManager.generateAndSign(bo);

            assertEquals(1L, reportId);
            // 验证 QR 码内容被设置
            verify(instanceMapper).updateById(argThat(inst ->
                inst.getQrCodeContent() != null
                && inst.getQrCodeContent().equals("CHRONIC-REPORT-1")
            ));
            // 验证事务提交后触发了异步 PDF 生成
            verify(reportPdfHelper).generatePdfAsync(1L);
        }
    }

    @Test
    void shouldThrowWhenInstanceNotFound() {
        ChReportInstanceBo bo = new ChReportInstanceBo();
        bo.setPatientId(1L);

        when(reportService.generateReport(bo)).thenReturn(99L);
        when(instanceMapper.selectById(99L)).thenReturn(null);

        // 异常在 registerSynchronization 之前抛出，无需 MockedStatic
        assertThrows(ServiceException.class, () -> {
            reportGenerateManager.generateAndSign(bo);
        });
    }

    @Test
    void shouldQueryReportDetail() {
        ChReportInstanceVo vo = new ChReportInstanceVo();
        when(reportService.queryReportById(1L)).thenReturn(vo);

        ChReportInstanceVo result = reportGenerateManager.queryDetail(1L);
        assertNotNull(result);
        verify(reportService).queryReportById(1L);
    }
}
