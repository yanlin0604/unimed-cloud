package org.dromara.chronic.manager;

import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.chronic.mapper.ChReportInstanceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ReportPdfHelper 测试
 * 验证异步 PDF 生成逻辑
 *
 * @author unimed
 */
@ExtendWith(MockitoExtension.class)
class ReportPdfHelperTest {

    @Mock
    private ChReportInstanceMapper instanceMapper;

    @InjectMocks
    private ReportPdfHelper reportPdfHelper;

    @Test
    void shouldGeneratePdfAndSign() {
        ChReportInstance instance = new ChReportInstance();
        instance.setReportId(1L);
        when(instanceMapper.selectById(1L)).thenReturn(instance);
        when(instanceMapper.updateById(any(ChReportInstance.class))).thenReturn(1);

        reportPdfHelper.generatePdfAsync(1L);

        verify(instanceMapper).updateById(argThat(inst ->
            inst.getPdfOssId() != null
            && inst.getPdfOssId().startsWith("PDF-1-")
            && inst.getSignTime() != null
        ));
    }

    @Test
    void shouldSkipWhenInstanceNotFound() {
        when(instanceMapper.selectById(99L)).thenReturn(null);

        reportPdfHelper.generatePdfAsync(99L);

        verify(instanceMapper, never()).updateById(any());
    }

    @Test
    void shouldSetFailedStatusWhenUpdateThrows() {
        ChReportInstance instance = new ChReportInstance();
        instance.setReportId(1L);
        when(instanceMapper.selectById(1L)).thenReturn(instance);
        // 第一次 updateById 调用（PDF生成）抛异常，第二次（标记FAILED）成功
        when(instanceMapper.updateById(any(ChReportInstance.class)))
            .thenThrow(new RuntimeException("DB error"))
            .thenReturn(1);

        // 不应抛出异常
        assertDoesNotThrow(() -> reportPdfHelper.generatePdfAsync(1L));

        // 验证 updateById 被调用两次
        ArgumentCaptor<ChReportInstance> captor = ArgumentCaptor.forClass(ChReportInstance.class);
        verify(instanceMapper, times(2)).updateById(captor.capture());

        // 第二次调用（catch块中）应设置 pushStatus=FAILED
        ChReportInstance secondCall = captor.getAllValues().get(1);
        assertEquals("FAILED", secondCall.getPushStatus());
    }
}
