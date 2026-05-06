package org.dromara.chronic.support.ocr;

/**
 * 百度医疗类 OCR 接口端点常量。
 * <p>
 * 百度 AIP 公开的 REST 端点，地址契约由百度维护，不应作为业务配置外露。
 * 若百度变更地址，统一在此处修改即可。
 *
 * @author unimed
 */
public final class BaiduOcrEndpoints {

    /** 病案首页 / 出院小结 */
    public static final String MEDICAL_RECORD_HOME = "https://aip.baidubce.com/rest/2.0/ocr/v1/medical_record";

    /** 检验报告 */
    public static final String LAB_REPORT = "https://aip.baidubce.com/rest/2.0/ocr/v1/medical_report_detection";

    /** 检查报告（影像类） */
    public static final String EXAM_REPORT = "https://aip.baidubce.com/rest/2.0/ocr/v1/medical_detection_report";

    /** 诊断证明 / 诊断报告 */
    public static final String DIAGNOSIS_REPORT = "https://aip.baidubce.com/rest/2.0/ocr/v1/medical_summary";

    private BaiduOcrEndpoints() {
    }
}
