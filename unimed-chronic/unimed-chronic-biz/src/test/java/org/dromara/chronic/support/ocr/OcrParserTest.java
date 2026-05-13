package org.dromara.chronic.support.ocr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.chronic.support.ocr.domain.BaiduOcrResponse;
import org.dromara.chronic.support.ocr.domain.OcrParseResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 医疗文档OCR解析器测试
 *
 * @author unimed
 */
public class OcrParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OcrArchiveMapper archiveMapper = new OcrArchiveMapper(objectMapper);
    private final OcrMetricMapper metricMapper = new OcrMetricMapper();
    private final OcrParser parser = new OcrParser(objectMapper, archiveMapper, metricMapper);

    @Test
    public void parseMedicalRecordHomeShouldBuildArchiveDraft() {
        String json = """
            {
              "姓名":"张三",
              "身份证号":"110101199001011234",
              "性别":"男",
              "医保类型":"城镇职工医保",
              "主要诊断":"2型糖尿病",
              "ICD编码":"E11.900"
            }
            """;

        OcrParseResult result = parser.parse(BaiduOcrResponse.success(json), "MEDICAL_RECORD_HOME", null);

        assertNotNull(result.getArchiveDraft());
        assertEquals("张三", result.getProfileDraft().getName());
        assertEquals("1", result.getProfileDraft().getGender());
        assertEquals(1, result.getDiseaseDrafts().size());
        assertEquals("E11.900", result.getDiseaseDrafts().get(0).getIcdCode());
    }

    @Test
    public void parseLabReportShouldBuildMetricDraft() {
        String json = """
            {
              "items":[
                {"项目名称":"空腹血糖","结果":"7.2","单位":"mmol/L","参考范围":"3.9-6.1","异常":"偏高"},
                {"项目名称":"尿酸","结果":"420","单位":"umol/L","参考范围":"208-428"}
              ]
            }
            """;

        OcrParseResult result = parser.parse(BaiduOcrResponse.success(json), "LAB_REPORT", 1001L);

        assertEquals(2, result.getMetricItems().size());
        assertEquals("BLOOD_GLUCOSE", result.getMetricItems().get(0).getMetricType());
        assertTrue(result.getMetricItems().get(0).getIsAbnormal());
        assertEquals("OCR", result.getMetricBos().get(0).getDataSource());
        assertEquals(1001L, result.getReportDraft().getPatientId());
    }
}

